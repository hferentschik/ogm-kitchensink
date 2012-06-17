/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.kitchensink.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.jboss.as.quickstarts.kitchensink.controller.MemberController;
import org.jboss.as.quickstarts.kitchensink.data.CriteriaMemberRepository;
import org.jboss.as.quickstarts.kitchensink.data.FullTextMemberRepository;
import org.jboss.as.quickstarts.kitchensink.data.MemberListProducer;
import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
import org.jboss.as.quickstarts.kitchensink.model.ContactDetails;
import org.jboss.as.quickstarts.kitchensink.model.Member;
import org.jboss.as.quickstarts.kitchensink.rest.JaxRsActivator;
import org.jboss.as.quickstarts.kitchensink.rest.MemberResourceRESTService;
import org.jboss.as.quickstarts.kitchensink.service.MemberRegistration;
import org.jboss.as.quickstarts.kitchensink.util.QueryHelper;
import org.jboss.as.quickstarts.kitchensink.util.Resources;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.junit.Test;

public abstract class MemberRegistrationTest {

    public final static String[] dependencyExclusions = {
        "org.hibernate:hibernate-search-analyzers",
        "org.hibernate:hibernate-core",
        "org.hibernate.javax.persistence:*",
        "org.hibernate.common:*",
        "org.jboss.logging:*",
        "org.jboss.shrinkwrap:*",
        "org.infinispan:*",
        "dom4j:*",
        "org.javassist:*",
        "org.javassist:*"
    };

    public static Archive<?> createTestArchive(String persistenceUnitConfigurationSource, String cdiBeansConfigurationSource, String warName) {
        MavenDependencyResolver resolver = DependencyResolvers.use(MavenDependencyResolver.class)
                .goOffline();// take SNAPSHOTS from your local cache: greatly speedups development
        return ShrinkWrap
                .create(WebArchive.class, warName)
                .addClasses(
                        MemberRegistrationTest.class,
                        MemberController.class,
                        MemberListProducer.class,
                        CriteriaMemberRepository.class,
                        FullTextMemberRepository.class,
                        MemberRepository.class,
                        ContactDetails.class,
                        Member.class,
                        JaxRsActivator.class,
                        MemberResourceRESTService.class,
                        MemberRegistration.class,
                        QueryHelper.class,
                        Resources.class,
                        FacesContextStub.class)
                .addAsResource(persistenceUnitConfigurationSource, "META-INF/persistence.xml")
                .addAsResource("infinispan.xml", "infinispan.xml")
                .addAsResource("infinispan-ogm-config.xml", "infinispan-ogm-config.xml")
                .addAsWebInfResource(cdiBeansConfigurationSource, "beans.xml")
                .addAsWebInfResource(new StringAsset("<faces-config version=\"2.0\"/>"), "faces-config.xml")
                .addAsWebInfResource("jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
                .addAsLibraries(
                        resolver
                            .artifact("org.hibernate:hibernate-search-orm:4.1.1.Final")
                            .exclusions(dependencyExclusions)
                            .resolveAs(JavaArchive.class)
                        )
                .addAsLibraries(
                        resolver
                            .artifact("org.hibernate:hibernate-search-infinispan:4.1.1.Final")
                            .exclusions(dependencyExclusions)
                            .resolveAs(JavaArchive.class)
                        )
                .addAsLibraries(
                        resolver
                            .artifact("org.infinispan:infinispan-lucene-directory:5.1.5.FINAL") //MUST MATCH the Infinispan version of the AS
                            .exclusions(dependencyExclusions)
                            .resolveAs(JavaArchive.class)
                        )
                ;
    }

    @Inject
    MemberController memberController;

    @Inject
    MemberListProducer membersRegister;

    @Inject
    Logger log;

    @Test
    public void testRegister() throws Exception {
        FacesContextStub.setCurrentInstance(new FacesContextStub("test"));

        List<Member> members = membersRegister.getMembers();
        assertEquals(0, members.size());

        Member newMember = memberController.getNewMember();
        newMember.setName("Jane Doe");

        ContactDetails newContactDetails = memberController.getContactDetails();
        newContactDetails.setEmail("jane@mailinator.com");
        newContactDetails.setPhoneNumber("2125551234");

        memberController.register();

        assertNotNull(newMember.getId());
        log.info(newMember.getName() + " was persisted with id " + newMember.getId());

        members = membersRegister.getMembers();
        assertEquals(1, members.size());

        //now test sorting on multiple names:
        memberController.initNewMember();
        newMember = memberController.getNewMember();
        newMember.setName("Alan Doe");
        newContactDetails = memberController.getContactDetails();

        newContactDetails.setEmail("alan@mailinator.com");
        newContactDetails.setPhoneNumber("3125551234");

        memberController.register();
        members = membersRegister.getMembers();
        assertEquals(2, members.size());

        assertEquals("Alan Doe", members.get(0).getName());
        assertEquals("Jane Doe", members.get(1).getName());
    }

}
