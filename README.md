# weather-reports
An application for weather reports programmed in Clojure. Functional logic and data manipulation

# Weather Report System: Functional Programming

### Overview
This project is a **Functional Programming (FP)** application designed to parse, transform, and analyze weather data. Developed in **Clojure**, it demonstrates the power of immutable data structures and declarative logic in processing real-world datasets. It was developed as an academic project at **Concordia University**.

### Project Focus
* **Functional Pipelines:** Leveraged Clojure's threading macros (`->>`) to create readable, efficient data processing pipelines.
* **Unit Transformation Logic:** Implemented math-heavy transformations for temperature scaling, including automatic normalization back to Celsius before file serialization.
* **Statistical Analysis:** Developed a data-aggregation engine that calculates averages, extremes (hottest/coldest), and unique conditions across datasets.
* **Thread-Safe State:** Utilized Clojure Atoms to manage the global temperature unit state, ensuring consistent UI rendering.

### Tech Stack
* **Language:** Clojure (Version 1.11)
* **Runtime:** JVM (Version 21)
* **Tools:** WSL (Ubuntu), VS Code, Clojure CLI, and Bash CLI.

#### Prerequisites
* **Java SDK 21**
* **Clojure CLI**

#### Installation & Execution
1. **Clone the repository:**
   ```bash
   git clone https://github.com/lnifakis/weather-reports.git
   cd weather-report-system
   ```
2. **Run the application:**
   If using the Clojure CLI:
   ```bash
   clojure weather.clj
   ```
   > **Note:** weather_data.txt must be included in the root directory for it to be properly loaded.
