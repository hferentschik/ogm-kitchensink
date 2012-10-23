# Prerequisites

## Required

* JDK 6
* [Maven 3](http://maven.apache.org/)
* [Git](http://git-scm.com/)

## Optional

* [rvm](http://beginrescueend.com/)
* [ruby](http://www.ruby-lang.org/en/)
* Required Gems :

        $ gem install httparty
        $ gem install nokogiri
        $ gem install choice


# Demo

## Ruby setup

        $ rvm gemset create ogm
        $ rvm gemset use ogm
        $ gem install httparty nokogiri choice

## Demo JPA version

* Switch to JPA using patch file

        $ git apply presentation/0001-Switch-to-JPA.patch

* Show structure and build
    * _persistence.xml_
    * Domain classes _Member_ and _ContactDetails_
    * _MemberRegistration_ and _MemberListProducer_
    * look no _web.xml_ :-)
* Run Arquillian test

        $ mvn test

* Deploy using cargo:
 
        $ mvn clean package cargo:run

* Demo site - [ogm-kitchensink](http://localhost:8080/ogm-kitchensink)
* Create some test members:

        $ ruby member-generator.rb -a http://localhost:8080/ogm-kitchensink -c 20

## Demo OGM version

* Switch to OGM (by reverting back patch changes)
       * Show AS custom module and how to enable it via _jboss-deployment-structure.xml_
       * Add dependencies to _pom.xml_
       * Change persistence provider in _persistence.xml_
       * Switch to UUID as entity ids
       * Switch to Search for querying
           * Need to add Search annotations
           * Switch from Criteria to Search in _beans.xml_  (via alternatives)
* Deploy:

        $  mvn clean package cargo:run

* Demo site - [ogm-kitchensink](http://localhost:8080/ogm-kitchensink)
* Create some test members:

        $ ruby member-generator.rb -a http://localhost:8080/ogm-kitchensink -c 20

## Demo OpenShift app creation and deployment

* Explain OpenShift
* Explain rvm
* Get the Openshift Express ruby scripts installed

        $ gem install rhc

* Use the rhc commands to create domain and applications (well, ogm domain is already created, use different one if you want to demo it)

        $ rhc-create-domain -n ogm
        $ rhc-create-app -a kitchensink -t jbossas-7 --nogit -l <user>

* Explain _openshift_ profile in _pom.xml_ (gets enabled during build on server side)
* Explain hooks and layout of _.openshift_
    * modules directory
    * _standalone.xml_
* Add the git repo created by rhc-create-app as remote to the demo

        $ git remote add openshift <repo-url>

* Push to Openshift

        $ git push -f openshift master

* Demo site - [ogm-kitchensink](http://kitchensink-ogm.rhcloud.com)
* Create some test members:

        $ ruby member-generator.rb -a http://kitchensink-ogm.rhcloud.com -c 20

* Cleanup

        $ rhc-ctl-app -a kitchensink -c destroy
        $ rhc-user-info
        $ rvm gemset delete ogm

# Links

* [AS 7 Command Line Interface](http://www.hibernate.org/subprojects/ogm.html)
* [Openshift documentation](https://www.redhat.com/openshift/documents)

