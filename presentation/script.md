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
* Switch to OGM (replacement code should be already in the source commented out)
       * Add dependencies to _pom.xml_
       * Change persistence provider in _persistence.xml_
       * Enable Hibernate 3 module (also _persistence.xml_)
       * Switch to UUID as entity ids (don't forget to switch the id type to _String_)
       * Switch to Search for querying
           * Need to add Search annotations
           * Switch from Criteria to Search in _beans.xml_  (via alternatives)
       * Enable displaying of Infinispan cache in _index.xhtml_ (optional)
* Deploy:

        $  mvn clean package cargo:run

* Demo site - [ogm-kitchensink](http://localhost:8080/ogm-kitchensink)
* Create some test members:

        $ ruby member-generator.rb -a http://localhost:8080/ogm-kitchensink -c 20

## Demo OpenShift app creation and deployment

* Explain OpenShift
* Explain rvm
* Get the Openshift Express ruby scripts installed

        $ rvm gemset create ogm
        $ rvm gemset use ogm
        $ gem install rhc
        // for the ruby script
        $ gem install httparty
        $ gem install nokogiri
        $ gem install choice

* Use the rhc commands to create domain and applications (well, ogm domain is already created, use different one if you want to demo it)

        $ rhc-create-domain -n ogm
        $ rhc-create-app -a kitchensink -t jbossas-7.0 --nogit

* Explain _openshift_ profile in _pom.xml_ (gets enabled during build on server side)
* Explain hooks and layout of _.openshift_
    * modules directory
    * _standalone.xml_
    * Caveat! - _standalone.xml_ contains IP addresses specific to a created app. If app gets destroyed and recreated the IPs need to be updated.
    The next release of OpenShift will allow to use variables. For this reason _action_hooks/build_  contains the line 'export' to dump env variables.
    This way we can view the IP needed to add to _standalone.xml_
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

