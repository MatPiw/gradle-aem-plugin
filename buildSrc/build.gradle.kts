repositories {
    mavenLocal()
    gradlePluginPortal()
    jcenter()
    maven { url = uri("https://plugins.gradle.org/m2/") }
    maven { url = uri("http://dl.bintray.com/cognifide/maven-public") }
    maven { url = uri("https://dl.bintray.com/neva-dev/maven-public") }
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.21")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:0.9.18")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.0.0-RC16")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
    implementation("com.neva.gradle:fork-plugin:3.1.4")
    implementation("net.researchgate:gradle-release:2.6.0")
}
