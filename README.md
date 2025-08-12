# 🛠️ Automation Testing Framework – CWD Limited (By Aditya)

## 📌 Project Overview
This repository contains **Automation Testing scripts** developed during my tenure at **CWD Limited**.  
The goal of this project was to **automate repetitive testing tasks**, improve testing efficiency, and ensure consistent quality in hardware/software integrated products.

The scripts are designed to work **cross-platform (Windows & Linux)** and use **Selenium WebDriver** with Java, Maven, and other dependencies for browser automation.

---

## 🎯 Why This Project?
At CWD Limited, testing involved:
- **Web Application UI Automation**
- **Functional Test Automation**
- **Role-Based User Creation & Validation**
- **Dynamic Data Handling** (e.g., Excel integration)
- **Screenshot Logging for Each Step**
- **Retry Mechanisms for Failures**
  
By automating these tasks, we reduced manual efforts, increased accuracy, and achieved faster test cycles.

---

## 📂 What's Inside
- **Multiple Automation Test Suites** – Each project folder contains automation scripts for specific modules or scenarios.
- **Maven Project Structure** – Ensures easy build and dependency management.
- **Dynamic Data Handling** – Some scripts read from Excel or other inputs.
- **Screenshot Capturing** – Each test step is documented with screenshots.
- **Empty Drivers Folder** – To be filled after downloading respective drivers.

---

## ⚙️ Requirements
Before running the scripts, ensure the following are installed:

### **Common Requirements**
- Java JDK 8+ installed & configured (`JAVA_HOME`)
- Maven installed & configured (`MAVEN_HOME`)
- Internet connection (to download dependencies)
- Browser installed (Google Chrome recommended)

### **Driver Binaries Required**
These drivers are required for running the automation scripts:
- **Apache Maven** – [Download Maven](https://maven.apache.org/download.cgi)
- **ChromeDriver (Windows/Linux)** – [Download ChromeDriver](https://chromedriver.chromium.org/downloads)
- **Apache POI** (For Excel operations) – Included via Maven dependencies
- **Chromium ChromeDriver** – For Chromium browser automation (optional)

⚠️ **Note:**  
The `drivers` folder inside each project is **empty**. You must **download the drivers** and **place them in the `drivers` folder** before running the scripts.

---

## 💻 Installation & Setup

### **1️⃣ Windows Setup**
1. **Install Java JDK**
   - Download: [Java JDK](https://www.oracle.com/java/technologies/javase-downloads.html)
   - Add to PATH:
     ```powershell
     setx JAVA_HOME "C:\Program Files\Java\jdk-<version>"
     setx PATH "%JAVA_HOME%\bin;%PATH%"
     ```

2. **Install Maven**
   - Extract Apache Maven (`apache-maven-3.9.10`) to a folder.
   - Add to PATH:
     ```powershell
     setx MAVEN_HOME "C:\path\to\apache-maven-3.9.10"
     setx PATH "%MAVEN_HOME%\bin;%PATH%"
     ```
   - Verify: `mvn -version`

3. **Install ChromeDriver**
   - Download from [ChromeDriver site](https://chromedriver.chromium.org/downloads) matching your Chrome version.
   - Extract and place in the `drivers` folder of the project.

4. **Apache POI**
   - Added via `pom.xml` – Maven will auto-download dependencies.

5. **Run the Tests**
   ```powershell
   mvn clean test


---

### **2️⃣ Linux Setup**

1. **Install Java JDK**

   ```bash
   sudo apt update
   sudo apt install default-jdk
   java -version
   ```

2. **Install Maven**

   ```bash
   sudo apt install maven
   mvn -version
   ```

3. **Install Chrome & ChromeDriver**

   ```bash
   sudo apt install wget unzip
   wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
   sudo dpkg -i google-chrome-stable_current_amd64.deb
   sudo apt --fix-broken install

   CHROME_VERSION=$(google-chrome --version | grep -oE "[0-9]+")
   wget https://chromedriver.storage.googleapis.com/<MATCHING_VERSION>/chromedriver_linux64.zip
   unzip chromedriver_linux64.zip
   mv chromedriver drivers/
   chmod +x drivers/chromedriver
   ```

4. **Apache POI**

   * Included in Maven dependencies; no manual install needed.

5. **Run the Tests**

   ```bash
   mvn clean test
   ```

---

## 📌 How to Run the Project

1. Clone the repository:

   ```bash
   git clone https://github.com/AdityaKonda6/Automation-Testing-CWD-By-Aditya.git
   ```
2. Navigate to a specific project folder.
3. Download and place the required drivers into the `drivers` folder.
4. Build & run:

   ```bash
   mvn clean test
   ```

---

## 📝 Notes

* Ensure **ChromeDriver version matches** your installed Chrome browser version.
* All **driver files are excluded** from the repository for security and compatibility reasons.
* This framework works on both **Windows and Linux** without code changes.
* Test results and screenshots are generated in the `target` folder after execution.

---

🧪 Technologies You Now Have Installed
```
| Tool                  | Purpose                               |
| --------------------- | ------------------------------------- |
| Java JDK              | For compiling and running Java        |
| Maven                 | For managing dependencies and build   |
| Chrome                | To open and test the website          |
| ChromeDriver          | Interface between Selenium and Chrome |
| IDE (VSCode/IntelliJ) | Easier coding and debugging           |
| Git                   | Version control and GitHub access     |
```

🔎 Troubleshooting Tips
```
| Problem                  | Fix                                                  |
| ------------------------ | ---------------------------------------------------- |
| `NoSuchElementException` | Check if IDs are changing dynamically                |
| Chrome doesn't open      | Check `chromedriver.exe` version matches Chrome      |
| `mvn` not recognized     | Check your Maven PATH setup                          |
| Test not doing anything  | Add `Thread.sleep()` or check dynamic loading (AJAX) |
```

---


<img align="right" height="250" width="375" alt="" src="https://github.com/AdityaKonda6/AdityaKonda6/blob/main/giphy2.webp" />

## Hey there 👋, I'm [<a href="https://adityakonda04.vercel.app/">Aditya!</a>](https://github.com/AdityaKonda6)

[![Linkedin Badge](https://img.shields.io/badge/-LinkedIn-0e76a8?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/aditya-adi-konda/)
[![Twitter Badge](https://img.shields.io/badge/-Twitter-00acee?style=flat-square&logo=Twitter&logoColor=white)](https://twitter.com/AdityaKonda7)
[![Instagram Badge](https://img.shields.io/badge/-Instagram-e4405f?style=flat-square&logo=Instagram&logoColor=white)](https://www.instagram.com/konda_aditya/)

### Glad to see you here! &nbsp; ![](https://visitor-badge.glitch.me/badge?page_id=adityakonda.adityakonda&style=flat-square&color=0088cc)

I’m a **2025 IT Graduate** passionate about **DevOps, Cloud, and Software Development** 🚀.  
My mission? To **bridge the gap between development and operations**—building scalable systems, automating workflows, and ensuring quality from code to deployment.

With a strong foundation in **Java, SQL, Linux**, and hands-on experience with **CI/CD pipelines, Selenium automation, cloud services, and Android development**, I thrive in solving problems end-to-end—from writing code to deploying it in production.

Recently, at **CWD Limited**, I worked on:
- **Automation Testing Frameworks** (Selenium, Java, Maven)
- **Linux-based system configurations & debugging**
- **Hardware-software integration testing**
- API testing with Postman  
…and in the process, strengthened my DevOps skill set.

💡 Curious mind. Fast learner. Always ready to build, break, and rebuild—better.

---

### 🚀 What I’m Working On:
- Building **DevOps projects** (Jenkins, Docker, Kubernetes, AWS, Ansible)
- Enhancing **automation frameworks** for testing & deployment
- Crafting **Android apps** and backend services
- Expanding my **Linux administration** skills

---

### 💼 My Tech Stack:
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/java/java.png" alt="Java"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/linux/linux.png" alt="Linux"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/docker/docker.png" alt="Docker"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/kubernetes/kubernetes.png" alt="Kubernetes"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/aws/aws.png" alt="AWS"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/python/python.png" alt="Python"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/javascript/javascript.png" alt="JavaScript"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/react/react.png" alt="React"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/sql/sql.png" alt="SQL"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/master/topics/git/git.png" alt="Git"></code>

---

<img align="right" height="250" width="375" alt="" src="https://raw.githubusercontent.com/iampavangandhi/iampavangandhi/master/gifs/coder.gif" />

### 📌 Highlights:
- 🛠 Built **dynamic Selenium automation scripts** integrated with Maven
- 🚀 Created & deployed **full-stack and Android applications**
- 🐧 Comfortable with **Linux system administration & shell scripting**
- 📦 Implemented CI/CD workflows for smoother deployments
- ☁️ Learning & applying **cloud infrastructure concepts**

---

### 📫 How to Reach Me:
- Email: **adityakonda04@gmail.com**
- Portfolio: [adityakonda04.vercel.app](https://adityakonda04.vercel.app/)
- LinkedIn: [Aditya Adi Konda](https://www.linkedin.com/in/aditya-adi-konda/)

---

### 📊 GitHub Stats:
<details>
  <summary><b>⚡ GitHub Stats</b></summary>
  <br />
  <img height="180em" src="https://github-readme-stats.vercel.app/api?username=adityakonda6&show_icons=true&hide_border=true&&count_private=true&include_all_commits=true" />
  <img height="180em" src="https://github-readme-stats.vercel.app/api/top-langs/?username=adityakonda6&show_icons=true&hide_border=true&layout=compact&langs_count=8"/>
</details>

<details>
  <summary><b>🔥 GitHub Streaks</b></summary>
  <br />
  <img height="180em" src="https://github-readme-streak-stats.herokuapp.com/?user=adityakonda6&hide_border=true" />
</details>

<details>
  <summary><b>☄️ LeetCode Stats</b></summary>
  <br />
   <p align="center"><img align="center" src="https://leetcard.jacoblin.cool/adityakonda04?theme=wtf&font=Coda%20Caption&ext=heatmap" /></p>
</details>

---

💬 Always open to collaborations, tech discussions, and exploring new opportunities in **DevOps, Cloud, and Software Development**.
