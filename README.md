# Yumme Server

[![Build Status][github-actions-image]][github-actions-url]
[![MIT license][license-image]][license-url]

[github-actions-image]: https://img.shields.io/github/workflow/status/aesy/yumme-server/Continous%20Integration?style=flat-square
[github-actions-url]: https://github.com/aesy/yumme-server/actions

[license-image]: https://img.shields.io/github/license/aesy/yumme-server?style=flat-square
[license-url]: https://github.com/aesy/yumme-server/blob/master/LICENSE

Backend for Yumme - a self-hosted recipe journal.

## Development

#### Prerequisites

* [Gradle 6.5+](https://gradle.org/)
* [A Java 11+ Runtime](https://adoptopenjdk.net/)
* [Docker](https://docs.docker.com/get-docker/) + [Docker Compose](https://docs.docker.com/compose/install/)
* [MySQL](https://www.mysql.com/downloads/) or [MariaDB](https://mariadb.org/download/)

#### Build

To compile and package the application, simply issue the following command:

$ `gradle build`

This will create an executable jar located in `build/libs/`.

#### Run

To run the application from the command line, the following command can be used:

$ `gradle bootRun`

This assumes a database to be accessible at `localhost:3306` (configurable through environment variables, 
see `src/main/resources/application.yml`). If you don't want to set one up for whatever reason, you can run 
the database in a Docker container. Docker compose can be used to easily run all necessary parts of the 
application without polluting the host they are running on. To run the application using docker-compose, 
use the following command in the root directory:

$ `docker-compose up` 

#### Test 

Run unit and integration tests with gradle:

$ `gradle test`

## Deployment

#### Image creation

To create an image, run the following command in the root directory:

$ `docker build . -t yumme-server`

An image is created and published automatically to github packages on git tag pushes.

## Contribute
Use the [issue tracker](https://github.com/aesy/yumme-server/issues) to report bugs or make feature 
requests. Pull requests are welcome, but it may be a good idea to create an issue to discuss any 
changes beforehand.

## License
MIT, see [LICENSE](/LICENSE) file.
