<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2020 - @FabioZumbi12
  ~ Last Modified: 26/07/2020 16:37.
  ~
  ~ This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
  ~  damages arising from the use of this class.
  ~
  ~ Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
  ~ redistribute it freely, subject to the following restrictions:
  ~ 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
  ~ use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
  ~ 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
  ~ 3 - This notice may not be removed or altered from any source distribution.
  ~
  ~ Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
  ~ responsabilizados por quaisquer danos decorrentes do uso desta classe.
  ~
  ~ É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
  ~ alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
  ~ 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
  ~  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
  ~ 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
  ~ classe original.
  ~ 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
  -->

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>io.github.fabiozumbi12.RedProtect</groupId>
        <artifactId>RedProtect</artifactId>
        <version>8.1.0</version>
        <relativePath>../../pom.xml</relativePath>
    </parent>
    <artifactId>RedBackups</artifactId>
    <name>RedBackups</name>

    <description>This plugin makes backups of redprotect player regions</description>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>16</source>
                    <target>16</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>2.3.1</version>
                <configuration>
                    <outputDirectory>../../add-ons</outputDirectory>
                </configuration>
            </plugin>
        </plugins>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>

    <repositories>
        <repository>
            <id>papermc</id>
            <url>https://papermc.io/repo/repository/maven-public/</url>
        </repository>
        <repository>
            <id>redprotect-repo</id>
            <url>https://raw.githubusercontent.com/FabioZumbi12/RedProtect/mvn-repo/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot-api</artifactId>
            <version>1.19.3-R0.1</version>
        </dependency>

        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot-api</artifactId>
            <version>1.19.3-R0.1</version>
            <classifier>javadoc</classifier>
        </dependency>

        <dependency>
            <groupId>io.github.fabiozumbi12.RedProtect</groupId>
            <artifactId>RedProtect-Core</artifactId>
            <version>8.1.0</version>
        </dependency>
        <dependency>
            <groupId>io.github.fabiozumbi12.RedProtect</groupId>
            <artifactId>RedProtect-Spigot</artifactId>
            <version>8.1.0</version>
        </dependency>
    </dependencies>
</project>
