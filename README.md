<a id="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<br />
<div align="center">
  <a href="https://github.com/alonsojj/CountBellyDash">
    <img src="href="/src/main/resources/logo.png" alt="Logo" width="100" height="100">
  </a>

<h3 align="center">Count Belly Dash</h3>

  <p align="center">
    Dashboard for a AI-powered WhatsApp bot
    <br />
    <a href="https://github.com/alonsojj/BellyCountBot"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/alonsojj/BellyCountBot/issues/new?labels=bug&template=bug-report---.md">Report Bug</a>
    ·
    <a href="https://github.com/alonsojj/BellyCountBot/issues/new?labels=enhancement&template=feature-request---.md">Request Feature</a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#project-structure">Project Structure</a></li>
    <li><a href="#environment-variables">Environment Variables</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

## About The Project

[![Belly Count Bot Screenshot][product-screenshot]](https://github.com/alonsojj/BellyCountBot)

**Count Belly Dash** is a Dashboar for Count Belly Bot

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

* [![Java][Java-badge]][Java-url]


<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

Follow these instructions to set up Count Belly Bot locally.

### Prerequisites

Ensure you have the following installed:

1. **Java JDK - 17**
   
   **Install:**
   ```powershell
   https://www.oracle.com/java/technologies/downloads/#java21
   ```

2. **CountBellyBot Instance**
   - Deploy Evolution API or use an existing instance
   - [Evolution API Documentation](https://github.com/alonsojj/CountBellyBot)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/alonsojj/CountBellyDash.git
   cd CountBellyDash
   ```



2. **Set up environment variables**
   ```bash
   cp .env.example .env
   ```
   
   Edit the `.env` file with your credentials:
   ```env
 
   ```
3. **Compile and execute**
   ```bash
   mvn compile exec:java
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



## Project Structure

```
BellyCountBot/
├── com.countbellydash/
│   │
│   ├── dao/                   
│   │   └── Conexao.java
│   │
│   ├── services/                    # Business logic layer
│   │   ├── BlacklistWhitelistService.java
│   │   ├── SessionService.java      
│   │   └── DashboardService.java    
│   │
│   ├── model/                   
│   │   ├── DashboardData.java
│   │   ├── SessionDto.java
│   │   └── DocumentType.java         
│   │
│   │
│   └── ui/                       
│       └── Main.java         
├── .env.example                     # Environment variables template
├── .gitignore
├── pom.xml
└── README.md
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Environment Variables

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `API_URL` |Url to Chatbo | ✅ | `https://localhost:8080` |
| `BOT_USERNAME` | Chatbot username Login | ✅ | `USERNAME` |
| `BOT_PASSWORD` |  Chatbot username Password  | ✅ | `PASSWORD` |
| `DB_URL` | Database url | ✅ | `postgresql://localhost:5432/postgres` |
| `DB_USER` | Database user | ✅ | `postgres` |
| `DB_PASSWORD` | Database password | ✅ | `PASSWORD` |




See the [open issues](https://github.com/alonsojj/BellyCountBot/issues) for a full list of proposed features and known issues.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better:

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



### Top Contributors

<a href="https://github.com/alonsojj/CountBellyDash/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=alonsojj/CountBellyDash" alt="contrib.rocks image" />
</a>

## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contact

your_email@email.com

Project Link: [https://github.com/alonsojj/CountBellyDash](https://github.com/alonsojj/CountBellyDash)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Acknowledgments

* [Java](https://www.java.com/pt-BR/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/alonsojj/CountBellyDash.svg?style=for-the-badge
[contributors-url]: https://github.com/alonsojj/CountBellyDash/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/alonsojj/CountBellyDash.svg?style=for-the-badge
[forks-url]: https://github.com/alonsojj/CountBellyDash/network/members
[stars-shield]: https://img.shields.io/github/stars/alonsojj/CountBellyDash.svg?style=for-the-badge
[stars-url]: https://github.com/alonsojj/CountBellyDash/stargazers
[issues-shield]: https://img.shields.io/github/issues/alonsojj/CountBellyDash.svg?style=for-the-badge
[issues-url]: https://github.com/alonsojj/CountBellyDash/issues
[license-shield]: https://img.shields.io/github/license/alonsojj/CountBellyDash.svg?style=for-the-badge
[license-url]: https://github.com/alonsojj/CountBellyDash/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/your_linkedin

[product-screenshot]: images/screenshot.png


[Java-badge]: https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white&style=for-the-badge
[Java-url]: https://www.java.com/pt-BR/




