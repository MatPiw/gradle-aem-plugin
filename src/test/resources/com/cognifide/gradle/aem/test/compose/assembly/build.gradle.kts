plugins {
    id("com.cognifide.aem.package")
    id("com.cognifide.aem.instance")
}

description = "Example"
defaultTasks = listOf(":aemSatisfy", ":aemDeploy")

allprojects { subproject ->
    group = "com.company.aem"
    version = "1.0.0-SNAPSHOT"

    repositories {
        maven { url = uri("https://repo.adobe.com/nexus/content/groups/public") }
        maven { url = uri("https://repo1.maven.org/maven2") }
        jcenter()
        mavenLocal()
    }

    plugins.withId("com.cognifide.aem.base") {
        aem {
            config {
                localInstance("http://localhost:4502")
                localInstance("http://localhost:4503")
            }
        }
    }

    plugins.withId("com.cognifide.aem.bundle") {

        tasks.withT
        jar {
            manifest {
                attributes(mapOf(
                        "Bundle-Name"    to subproject.description,
                        "Bundle-Category"to "example",
                        "Bundle-Vendor"  to "Company"
                ))
            }
        }

        dependencies {
            compile group: "org.slf4j", name: "slf4j-api", version: "1.5.10"
            compile group: "org.osgi", name: "osgi.cmpn", version: "6.0.0"
        }
    }
}

aemSatisfy {
    packages {
        group("dependencies") {
            // local("pkg/vanityurls-components-1.0.2.zip")
        }

        group("tools") {
            url("https://github.com/OlsonDigital/aem-groovy-console/releases/download/9.0.1/aem-groovy-console-9.0.1.zip")
            url("https://github.com/Cognifide/APM/releases/download/cqsm-2.0.0/apm-2.0.0.zip")
        }
    }
}

aemCompose {
    includeProject(":common")
    includeProject(":core")
    includeProject(":design")
}
