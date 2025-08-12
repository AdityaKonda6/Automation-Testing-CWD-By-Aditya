# 🔐 Login Automation Testing - For Any Website

This project automates login testing for the [Soundbox](https://soundbox.dev.sb.cwdin.com/login) web application using **Java**, **Selenium WebDriver**, **TestNG**, and **Maven**.
But you can change the url and do the login automation testing for any website
---

## ✅ Features

- 🔄 Performs **15 valid login attempts** with different credentials
- ❌ Performs **15 invalid login attempts**
- 📸 Takes **screenshots** for each attempt
- 🧪 Uses **TestNG** for test organization and assertions
- 📂 Stores all screenshots in a `screenshots/` folder

---

## 📁 Project Structure
```
SoundboxLoginTest/
├── drivers/ # ChromeDriver executable
├── screenshots/ # Screenshot output directory
├── src/
│ ├── main/
│ │ └── java/
│ │ └── config/ # Configuration class (e.g., driver path)
│ ├── test/
│ │ ├── java/
│ │ │ ├── pages/ # Page Object Model for Login Page
│ │ │ └── tests/ # Test classes with logic
├── pom.xml # Maven configuration
└── README.md # You're reading it!

```
---

## 🔧 Prerequisites

Ensure the following are installed:

- [Java 17+](https://adoptium.net/) (Tested with Java 21)
- [Apache Maven](https://maven.apache.org/)
- [Google Chrome (latest)](https://www.google.com/chrome/)
- [ChromeDriver](https://sites.google.com/a/chromium.org/chromedriver/downloads) (matching your Chrome version)

> ⚠️ Place the `chromedriver.exe` inside the `drivers/` folder.

---

## 🚀 How to Run 
---
For LoginTest.java -> mvn test -Dtest=LoginTest
---
For AddUserTest.java -> mvn test -Dtest=AddUserTest
---
### Step 1: Clone the Repo

```bash
git clone https://github.com/your-username/SoundboxLoginTest.git
cd SoundboxLoginTest

Object[][] validCreds = {
    {"user1@example.com", "password1"},
    {"user2@example.com", "password2"},
    ...
};

mvn clean test

| Tool         | Version            |
| ------------ | ------------------ |
| Java         | 17+ (tested on 21) |
| Maven        | 3.8+               |
| Selenium     | 4.20.0             |
| TestNG       | 7.8+               |
| Chrome       | 138+               |
| ChromeDriver | Match Chrome       |


---

```

✅ 1. Install Java Development Kit (JDK)
🔗 Download: Adoptium Java 17+

⚙️ After installation:

Set environment variable:
JAVA_HOME = C:\Program Files\Eclipse Adoptium\jdk-21 (or your install path)

Add to Path:
%JAVA_HOME%\bin


🔍 Verify:
```
bash
Copy
Edit
java -version
```
✅ 2. Install Apache Maven
🔗 Download: Maven Downloads

📂 Extract and set environment variables:
MAVEN_HOME = C:\apache-maven-3.9.x
Add to Path: %MAVEN_HOME%\bin

🔍 Verify:
```
bash
Copy
Edit
mvn -version
```
✅ 3. Install Google Chrome
🔗 Download Chrome

📌 Make sure it is updated to the latest version

✅ 4. Download Matching ChromeDriver
🔗 Check your Chrome version:
```
Go to chrome://settings/help
```
🔗 Download matching driver:
ChromeDriver Downloads

📁 Place chromedriver.exe in your project’s drivers/ folder
Or add it to the system PATH.

✅ 5. Install an IDE (Optional but Recommended)
💡 Recommended: Visual Studio Code or IntelliJ IDEA Community Edition

✅ 6. Clone the Project from GitHub
```
bash
Copy
Edit
git clone https://github.com/your-username/SoundboxLoginTest.git
cd SoundboxLoginTest
```
✅ 7. Build and Run the Project
```
Open terminal inside project folder:
bash
Copy
Edit
mvn clean test
```
✅ 8. (Optional) Install Git if you don’t have it
🔗 Git Downloads

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

If you'd like help adding a GitHub Actions CI workflow or TestNG HTML reporting, just say the word!

<img align="right" height="250" width="375" alt="" src="https://github.com/AdityaKonda6/AdityaKonda6/blob/main/giphy2.webp" />

## Hey there 👋, I'm [<a href="https://adityakonda04.vercel.app/">Aditya!</a>]([https://github.com/AdityaKonda6])

[![Twitter Badge](https://img.shields.io/badge/-Twitter-00acee?style=flat-square&logo=Twitter&logoColor=white)](https://twitter.com/AdityaKonda7)
[![Linkedin Badge](https://img.shields.io/badge/-LinkedIn-0e76a8?style=flat-square&logo=Linkedin&logoColor=white)](https://www.linkedin.com/in/aditya-adi-konda/)
[![Instagram Badge](https://img.shields.io/badge/-Instagram-e4405f?style=flat-square&logo=Instagram&logoColor=white)](https://www.instagram.com/konda_aditya/)

### Glad to see you here! &nbsp; ![](https://visitor-badge.glitch.me/badge?page_id=adityakonda.adityakonda&style=flat-square&color=0088cc)

I want to be a Software Developer but I am currently in college studying in IT with a mission to revolutionize how the world hires with AI. 🚀

I have a passion for problem-solving and pioneering first-principle thinking, driven by purpose and fueled by the challenge of finding solutions, with a strong foundation in Java, Python, SQL etc. I've led diverse tech teams covering Middleware, Backend and building scalable products from scratch with the commitment to making a positive impact through technology.

I'm an explorer of new tech, an avid learner, and a problem-solver at heart. When I'm not in code, you'll find me watching tech-related YouTube videos, fueling my curiosity.

Feel free to connect with me @kondaaditya for all things tech or just to say hello! Let's shape the future of tech together. 🌟

Joined Github **3** years ago.

Since then I pushed **20**+ commits, opened **15**+ issues, submitted **3**+ pull requests, created **64**+ gists and contributed to **18**+ public repositories.

Like My Work?

<a href="https://www.buymeacoffee.com/adityakonda04" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" height="60px" width="217px" ></a>

<p align="left"> <img src="https://komarev.com/ghpvc/?username=AdityaKonda6&label=Profile%20views&color=0e75b6&style=flat" alt="AdityaKonda6" /> </p>

<p align="left"> <a href="https://github.com/ryo-ma/github-profile-trophy"><img src="https://github-profile-trophy.vercel.app/?username=AdityaKonda6" alt="AdityaKonda6" /></a> </p>

<img align="right" height="250" width="375" alt="" src="https://raw.githubusercontent.com/iampavangandhi/iampavangandhi/master/gifs/coder.gif" />

### Talking about Personal Stuff:

- 🛠 &nbsp; I’m currently working with Java, SQL, Springboot, Python etc.
- 🚀 &nbsp; I’m currently exploring ML, Gen AI, etc.
- 💬 &nbsp; Ask me anything [here](https://github.com/AdityaKonda6/MyGitHub-Intro/issues)! I am happy to help.
- 👾 &nbsp; Fun fact: Equal is Not Always Equal in JS.
- 📫 &nbsp; Reach me out: adityakonda04@gmail.com.

### My Absolute Favorites:

- 💻 &nbsp; I love exploring new technologies and building cool stuff.
- 📰 &nbsp; Reading, writing & watching Tech Stuff whenever possible.
- 🍕 &nbsp; Meetups & Tech Events & Hackathons.

### Languages and Tools:

<code><img height="27" src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/javascript/javascript.png" alt="javascript"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/typescript/typescript.png" alt="typescript"></code>
<code><img height="30" src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/python/python.png" alt="python"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/nodejs/nodejs.png" alt="nodejs"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/aws/aws.png" alt="aws"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/react/react.png" alt="react"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/sql/sql.png" alt="sql"></code>
<code><img height="27" src="https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSTTzPAw-55ssm1Im594xYZ9eRQu2JylrkYLg&usqp=CAU" alt="mongodb"></code>
<code><img height="27" src="https://raw.githubusercontent.com/devicons/devicon/master/icons/git/git-original.svg" alt="git"></code>
<code><img height="27" src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/terminal/terminal.png" alt="terminal"></code>
<img align="right" height="250" width="375" alt="" src="https://github.com/AdityaKonda6/AdityaKonda6/blob/main/giphy.gif" />

### Projects and Dev Stuffs:

<details>
  <summary><b>⚡ Github Stats</b></summary>

  <br />
  <img height="180em" src="https://github-readme-stats.vercel.app/api?username=adityakonda6&show_icons=true&hide_border=true&&count_private=true&include_all_commits=true" />
  <img height="180em" src="https://github-readme-stats.vercel.app/api/top-langs/?username=adityakonda6&exclude_repo=KNN-Image-Classification&show_icons=true&hide_border=true&layout=compact&langs_count=8"/>
</details>

<details>
  <summary><b>☄️ Github Streaks</b></summary>

  <br />
  <img height="180em" src="https://github-readme-streak-stats.herokuapp.com/?user=adityakonda6&hide_border=true" />
</details>
<details>
  <summary><b>☄️ LeetCode Stats</b></summary>

  <br />
   <p align="center"><img align="center" src="https://leetcard.jacoblin.cool/adityakonda04?theme=wtf&font=Coda%20Caption&ext=heatmap" /></p>
   </details>
<details>
  <br />
  <summary><b>⚙️ Things I use to get stuff done</b></summary>
  	<ul>
  	    <li><b>OS:</b> Windows 10 Home</li>
	    <li><b>Laptop: </b> Lenovo Ideapad 320</li>
  	    <li><b>Browser: </b> Chrome </li>
	    <li><b>Terminal: </b> ZSH: Oh My Zsh (PowerLevel10k)</li>
	    <li><b>Code Editor:</b> VSCode - The best editor out there</li>
	    <li><b>To Stay Updated:</b> Twitter, Product Hunt and Hacker News</li>
	</ul>
</details>
<br>
