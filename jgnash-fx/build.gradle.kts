description = "jGnash"

val javaFXVersion: String by project    // extract JavaFX version from gradle.properties
val picocliVersion: String by project
val testFxVersion: String by project
val monocleVersion: String by project
val commonsLangVersion: String by project
val commonsMathVersion: String by project

val junitVersion: String by project
val junitExtensionsVersion: String by project
val awaitilityVersion: String by project

plugins {
    id("org.openjfx.javafxplugin")
    application // creates a task to run the full application
    `java-library`
}

application {
    mainClassName = "jgnash.app.jGnash"
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.github.glytching:junit-extensions:$junitExtensionsVersion")
    testImplementation("org.awaitility:awaitility:$awaitilityVersion")

    implementation(project(":jgnash-resources"))
    implementation(project(":jgnash-core"))
    implementation(project(":jgnash-convert"))
    implementation(project(":jgnash-report-core"))
    implementation(project(":jgnash-plugin"))

    implementation("info.picocli:picocli:$picocliVersion")

    implementation("org.apache.commons:commons-lang3:$commonsLangVersion")
    implementation("org.apache.commons:commons-math3:$commonsMathVersion")

    // Hack to include all javafx platforms in the classpath
    // The platform specific libraries are excluded when the distribution is assembled
    implementation("org.openjfx:javafx-base:$javaFXVersion")
    implementation("org.openjfx:javafx-fxml:$javaFXVersion")
    implementation("org.openjfx:javafx-controls:$javaFXVersion")
    implementation("org.openjfx:javafx-graphics:$javaFXVersion")
    implementation("org.openjfx:javafx-media:$javaFXVersion")
    implementation("org.openjfx:javafx-swing:$javaFXVersion")
    implementation("org.openjfx:javafx-web:$javaFXVersion")

    runtimeOnly("org.openjfx:javafx-base:$javaFXVersion:linux")
    runtimeOnly("org.openjfx:javafx-fxml:$javaFXVersion:linux")
    runtimeOnly("org.openjfx:javafx-controls:$javaFXVersion:linux")
    runtimeOnly("org.openjfx:javafx-graphics:$javaFXVersion:linux")
    runtimeOnly("org.openjfx:javafx-media:$javaFXVersion:linux")
    runtimeOnly("org.openjfx:javafx-swing:$javaFXVersion:linux")
    runtimeOnly("org.openjfx:javafx-web:$javaFXVersion:linux")

    runtimeOnly("org.openjfx:javafx-base:$javaFXVersion:win")
    runtimeOnly("org.openjfx:javafx-fxml:$javaFXVersion:win")
    runtimeOnly("org.openjfx:javafx-controls:$javaFXVersion:win")
    runtimeOnly("org.openjfx:javafx-graphics:$javaFXVersion:win")
    runtimeOnly("org.openjfx:javafx-media:$javaFXVersion:win")
    runtimeOnly("org.openjfx:javafx-swing:$javaFXVersion:win")
    runtimeOnly("org.openjfx:javafx-web:$javaFXVersion:win")

    runtimeOnly("org.openjfx:javafx-base:$javaFXVersion:mac")
    runtimeOnly("org.openjfx:javafx-fxml:$javaFXVersion:mac")
    runtimeOnly("org.openjfx:javafx-controls:$javaFXVersion:mac")
    runtimeOnly("org.openjfx:javafx-graphics:$javaFXVersion:mac")
    runtimeOnly("org.openjfx:javafx-media:$javaFXVersion:mac")
    runtimeOnly("org.openjfx:javafx-swing:$javaFXVersion:mac")
    runtimeOnly("org.openjfx:javafx-web:$javaFXVersion:mac")
    // end hack

    // required of Unit testing JavaFX
    testImplementation("org.testfx:testfx-junit5:$testFxVersion")
    testImplementation("org.testfx:openjfx-monocle:$monocleVersion")
}

javafx {
    version = javaFXVersion
    modules("javafx.base", "javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing",
            "javafx.graphics", "javafx.media")
}

tasks.test {
    useJUnitPlatform()

    // we want display the following test events
    testLogging {
        events("PASSED", "STARTED", "FAILED", "SKIPPED")
        showStandardStreams = true
    }
}

tasks.startScripts {
    applicationName = "bootloader"
}

tasks.distZip {
    destinationDirectory.set(file(rootDir))

    // this "should" work according to Gradle Doc but mangles the content of the zip file
    //archiveFileName.set("jgnash-${archiveVersion.get()}-bin.${archiveExtension.get()}")

    // build the mt940 plugin prior to creating the zip file without creating a circular loop
    dependsOn(":mt940:jar")

    // add the mt940 plugin
    into("jGnash-${archiveVersion.get()}") {
        from("../mt940/build/libs")
        include("*")
        into("jGnash-${archiveVersion.get()}/plugins")
    }

    into("jGnash-${archiveVersion.get()}") {
        from(".")
        include("scripts/*")
    }

    doLast {
        // delete the old renamed build
        file("${destinationDirectory.get()}/jgnash-${archiveVersion.get()}-bin.${archiveExtension.get()}").delete()

        file("${destinationDirectory.get()}/${archiveFileName.get()}").renameTo(file("${destinationDirectory.get()}/jgnash-${archiveVersion.get()}-bin.${archiveExtension.get()}"))
    }
}

distributions {
    main {
        distributionBaseName.set("jGnash")

        contents {
            from ( "../jgnash-manual/src/Manual.pdf" )
            from ( "../changelog.adoc" )
            from ( "../rust-launcher/target/release/jGnash.exe" )
            from ( "../README.html" )
            from ( "../README.adoc" )
            from ( "../jGnash" )
            exclude("**/*-linux*")  // excludes linux specific JavaFx modules from cross platform zip
            exclude("**/*-win*")    // excludes windows specific JavaFx modules from cross platform zip
            exclude("**/*-mac*")    // excludes mac specific JavaFx modules from cross platform zip
        }
    }
}

/**
 * Returns a proper Class-Path entry for the manifest file
 * @return classpath relative to the installation root point to the jars in the lib directory
 */
fun generateManifestClassPath(): String {
    val path = StringBuilder()

    configurations.runtimeClasspath.get().files.forEach {
        path.append("lib/")
        path.append(it.name)
        path.append(" ")
    }

    return path.toString()
}

tasks.jar {
    // Keep jar clean:
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.MF")

    manifest {
        attributes(mapOf("Main-Class" to "jGnash", "Class-Path" to generateManifestClassPath()))
    }
}
