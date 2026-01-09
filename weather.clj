;; Weather Report System
;; Written by: Liam Nifakis

(ns weather
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io]))


(declare main-menu handle-choice)


(defn parse-line
  "Parses a comma-separated line into a weather report map."
  [line]
  (let [[date location temp condition] (str/split line #",")]
    {:date date
     :location location
     :temperature (Integer/parseInt temp)
     :condition condition}))

(def temperature-unit (atom :unit)) ; Unit of temperature, default celcius (since it is not included in the file)

(defn set-temperature-unit! [unit]
  (reset!  temperature-unit unit))

(defn get-temperature-unit []
  @temperature-unit)


(defn load-reports
  "Loads weather reports from a file.
  Please refer to https://clojure.org/guides/threading_macros for more details on ->>
  "
  [filename]
  (set-temperature-unit! "˚C") ; Default temperature unit to Celcius
  (if (.exists (java.io.File. filename))
    (->> (slurp filename)
         str/split-lines
         (map parse-line)
         vec) 
    []))

(defn save-reports
  "Saves the list of reports to the file"
  [reports]
  (if (not= (get-temperature-unit) "˚C")
    (do
      (println "Warning: Reports are currently stored in Fahrenheit. The data will be transformed to Celcius before saving.") 
      (set-temperature-unit! "˚C") 
      (let [updated (map #(update % :temperature (fn [f] (int (* (- f 32) (/ 5.0 9))))) reports)] ; Saves an updated version of the reports  
        (println "Now saving reports.") 
        (let [file "weather_data.txt"] 
          (with-open [writer (io/writer file)] 
            (doseq [report updated] 
              (.write writer (str (str/join "," [(get report :date) 
                                                 (get report :location) 
                                                 (get report :temperature) 
                                                 (get report :condition)]) "\n")))))))
  
  (do
    (println "Now saving reports.")
    (let [file "weather_data.txt"]
      (with-open [writer (io/writer file)]
        (doseq [report reports]     ; Else, save the reports as-is.
          (.write writer (str (str/join "," [(get report :date)
                                             (get report :location)
                                             (get report :temperature)
                                             (get report :condition)]) "\n"))))))))

(defn center
  "Helper method; centers string s in a field of width w."
  [s w]
  (let [s (str s)
        pad (max 0 (- w (count s)))
        left (int (Math/floor (/ pad 2)))
        right (- pad left)]
    (str (apply str (repeat left " "))     ; Repeatedly adds spaces 
         s                                 ; Then adds string
         (apply str (repeat right " "))))) ; Then adds spaces again

(defn view-weather-reports
  "Display weather reports in a tabular format."
  [reports]
  (if (empty? reports)
    (println "No reports to display!")
    (let [items (count reports)]
      (println (str "Total weather reports: " items))
      (println "--------------------------------------------------------")
      (println "    Date     |   Location   | Temperature  |  Condition ")
      (println "--------------------------------------------------------")
      (doseq [r reports]
        (println
         (str
          (center (:date r) 12) " | "
          (center (:location r) 12) " | "
          (center (str (:temperature r) (get-temperature-unit)) 12) " | "
          (center (:condition r) 12)))))))


(defn filter-weather-reports
  "Filters weather reports based on user input."
  [reports]
  (println "Filter by:")
  (println "1. Condition")
  (println "2. Temperature Range")
  (print "Enter your choice (1-2): ")
  (flush)
  (let [choice (read-line)]
    (cond
      (= choice "1")
      (do
        (print "Enter condition to filter by: ")
        (flush)
        (let [condition (read-line)]
          (view-weather-reports (filter #(= (str/lower-case (:condition %)) (str/lower-case condition)) reports)))) ; Ignores case sensitivity
      (= choice "2")
      (do
        (print "Enter min temperature: ")
        (flush)
        (let [min (Integer/parseInt (read-line))]
          (print "Enter max temperature: ")
          (flush)
          (let [max (Integer/parseInt (read-line))]
            (view-weather-reports (filter #(and (>= (:temperature %) min) (<= (:temperature %) max)) reports)))))
      :else
      (println "Invalid choice."))))


(defn transform-weather-reports
  "Transforms weather reports between ˚C and ˚F"
  [reports]
  (println "Choose a transformation: ")
  (println "1. Convert temperatures to Farenheit")
  (println "2. Convert temperatures to Celcius")
  (print "Enter your choice (1-2): ")
  (flush)
  (let [choice (read-line)]
    (cond
      (= choice "1") (if (not= (get-temperature-unit) "˚F") 
                       (let [updated (map #(update % :temperature (fn [c] (int (+ (* 1.8 c) 32)))) reports)] 
                         (set-temperature-unit! "˚F") 
                         (view-weather-reports updated) 
                         updated) 
                       (do
                         (println "Reports are already in Fahrenheit.")
                         reports))
      (= choice "2") (if (not= (get-temperature-unit) "˚C")
                       (let [updated (map #(update % :temperature (fn [f] (int (* (- f 32) (/ 5.0 9))))) reports)] 
                         (set-temperature-unit! "˚C") 
                         (view-weather-reports updated) 
                         updated)
                       (do 
                         (println "Reports are already in Celcius.")
                         reports))
                       :else
                       (do
                         (println "Invalid choice.")
                         reports))))


(defn weather-statistics
  "Displays the average temperature, hottest and coldest days, and unique weather conditions."
  [reports]
  (let [temps (map :temperature reports)
        avg-temp (if (empty? temps) 0 (/ (reduce + temps) (count temps)))]
    (println (str "Average Temperature: " avg-temp)))
  (let [hottest (apply max-key :temperature reports)]
    (println (str "Hottest Day: " (:location hottest) " at " (:date hottest) " with " (:temperature hottest) (get-temperature-unit) " (" (:condition hottest) ")")))
  (let [coldest (apply min-key :temperature reports)]
    (println (str "Coldest Day: " (:location coldest) " at " (:date coldest) " with " (:temperature coldest) (get-temperature-unit) " (" (:condition coldest) ")")))
  (let [unique-conditions (distinct (map :condition reports))]
    (println "Unique Weather Conditions:")
    (doseq [condition unique-conditions]
      (println (str "   - " condition)))))


(defn exit-program []
  (println "\nThank you for using the Weather Report System. Goodbye!")
  (System/exit 0))


(defn main-menu
  ([file]
   (main-menu file (load-reports file)))
  ([file reports]
   (println "\n=== Weather Report System ===")
   (println "1. View Weather Reports")
   (println "2. Transform Weather Report")
   (println "3. Filter Weather Reports")
   (println "4. Weather Statistics")
   (println "5. Save and Exit")
   (print "Enter your choice (1-5): ")
   (flush)
   (let [choice (read-line)]
     (handle-choice choice reports file))))


(defn handle-choice [choice reports file]
  (case choice
    "1" (do (view-weather-reports reports)
            (main-menu file reports))
    "2" (let [updated (transform-weather-reports reports)]
          (main-menu file updated))
    "3" (do (filter-weather-reports reports)
            (main-menu file reports))
    "4" (do (weather-statistics reports)
            (main-menu file reports))
    "5" (do (save-reports reports)
            (exit-program))
    (do (println "Invalid option. Try again.")
        (main-menu file reports))))


;; Entry point
(defn -main [& args]
  (let [file "weather_data.txt"]
    (main-menu file)))

(-main)


