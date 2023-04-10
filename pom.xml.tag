<!--
  ~ Copyright (c) 2020 - @FabioZumbi12
  ~ Last Modified: 12/07/2020 18:57.
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
    <groupId>io.github.fabiozumbi12.RedProtect</groupId>
    <artifactId>RedProtect</artifactId>
    <packaging>pom</packaging>
    <name>RedProtect</name>
    <version>8.1.0</version>

    <scm>
        <connection>scm:git:git@github.com:FabioZumbi12/RedProtect.git</connection>
      <tag>RedProtect-8.1.0</tag>
  </scm>

    <distributionManagement>
        <snapshotRepository>
            <id>ossrh</id>
            <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
        </snapshotRepository>
    </distributionManagement>

    <build>
        <finalName>${project.name}-${project.version}</finalName>
        <resources>
            <resource>
                <targetPath>.</targetPath>
                <filtering>true</filtering>
                <directory>src/main/resources/</directory>
            </resource>
        </resources>

        <plugins>

            <!-- sign packages -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-gpg-plugin</artifactId>
                <version>1.6</version>
                <executions>
                    <execution>
                        <id>spigot-plugins</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>sign</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Parse Version to plugin.yml -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <version>1.10</version>
                <executions>
                    <execution>
                        <id>parse-version</id>
                        <goals>
                            <goal>parse-version</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>templating-maven-plugin</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <id>filter-src</id>
                        <goals>
                            <goal>filter-sources</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>16</source>
                    <target>16</target>
                </configuration>
            </plugin>

            <plugin>
                <artifactId>maven-resources-plugin</artifactId>
                <version>2.5</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                    <nonFilteredFileExtensions>
                        <nonFilteredFileExtension>schematic</nonFilteredFileExtension>
                        <nonFilteredFileExtension>schem</nonFilteredFileExtension>
                    </nonFilteredFileExtensions>
                </configuration>
            </plugin>

            <!-- Deploy to maven central for maven repo -->
            <plugin>
                <groupId>org.sonatype.plugins</groupId>
                <artifactId>nexus-staging-maven-plugin</artifactId>
                <version>1.6.13</version>
                <extensions>true</extensions>
                <configuration>
                    <serverId>ossrh</serverId>
                    <nexusUrl>https://s01.oss.sonatype.org/</nexusUrl>
                    <autoReleaseAfterClose>true</autoReleaseAfterClose>
                </configuration>
            </plugin>

            <!-- Javadoc and sources -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>3.2.0</version>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals>
                            <goal>jar-no-fork</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <version>2.9.1</version>
                <executions>
                    <execution>
                        <id>attach-javadocs</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>
    <modules>
        <module>RedProtect-Core</module>
        <module>RedProtect-Spigot</module>
        <!-- Addons -->
        <module>Addons/RedBackups</module>
        <module>Addons/KillerProjectiles</module>
        <module>Addons/RegionChat</module>
        <module>Addons/BuyRentRegion</module>
    </modules>
</project>