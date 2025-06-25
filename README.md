# WebMorph permission

A no-nonsense Spring Boot starter that embeds the standalone LuckPerms engine directly into your WebMorph ecosystem.

<p align="center">
<a href="https://github.com/web-morph/permission?tab=LGPL-3.0-1-ov-file"><img alt="License" src="https://img.shields.io/github/license/web-morph/permission"></a>
<a href="https://docs.gradle.org/8.14/release-notes.html"><img src="https://img.shields.io/badge/Gradle-8.14-brightgreen.svg?colorB=469C00&logo=gradle"></a>
<a href="https://repo.jyraf.com/service/rest/v1/search/assets/download?sort=version&repository=maven-releases&maven.groupId=com.github.webmorph&maven.artifactId=permission&maven.extension=jar&maven.classifier=" target="_blank"><img alt="Download" src="https://img.shields.io/nexus/r/com.github.webmorph/permission?server=https%3A%2F%2Frepo.jyraf.com"></a>
</p>

---

## ⚙️ Requirements

* Java 17 or above

## 📦 Installation

⚙️ Gradle (Kotlin DSL – build.gradle.kts)

```kts
repositories {
    maven("https://repo.jyraf.com/repository/maven-public/")
}

dependencies {
    implementation("com.github.webmorph:permission:<version>")
}
```

⚙️ Gradle (Groovy DSL – build.gradle)

```groovy
repositories {
    maven {
        url 'https://repo.jyraf.com/repository/maven-public/'
    }
}

dependencies {
    implementation "com.github.webmorph:permission:<version>"
}
```

------

# 🛠️ Contributing

Contributions are welcome! Feel free to open an issue or submit a pull request.

## 🧍 Author

### [CKATEPTb](https://github.com/CKATEPTb), [fakeivchenko](https://github.com/fakeivchenko)
