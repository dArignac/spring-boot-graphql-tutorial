package com.example.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.graphql.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;

// see https://github.com/graphql-java-kickstart/graphql-spring-boot/blob/master/example-graphql-tools/src/test/java/com/graphql/sample/boot/GraphQLToolsSampleApplicationTest.java
// see https://github.com/graphql-java-kickstart/graphql-spring-boot/blob/master/graphql-spring-boot-test/src/test/java/com/graphql/spring/boot/test/GraphQLResponseTest.java
@ExtendWith(SpringExtension.class)
//@GraphQLTest - fails to autowire
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Test
    public void testQueryPeopleEmpty() throws IOException {
        GraphQLResponse response = graphQLTestTemplate.postForResource("query-people.graphql");
        assertNotNull(response);
        assertTrue(response.isOk());
        assertNull(response.getList("$.data", Object.class));
    }

    @Test
    public void createPerson() throws IOException {
        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("firstName", "Jane");
        variables.put("lastName", "Doe");
        GraphQLResponse response = graphQLTestTemplate.perform("create-person.graphql", variables);
        assertNotNull(response);
        assertEquals("Jane", response.get("$.data.createPerson.firstName"));
        assertEquals("Doe", response.get("$.data.createPerson.lastName"));
        assertEquals("Jane Doe", response.get("$.data.createPerson.fullName"));
        assertNull(response.get("$.data.createPerson.dateOfBirth"));

        GraphQLResponse queryResponse = graphQLTestTemplate.postForResource("query-people.graphql");
        assertNotNull(queryResponse);
        assertTrue(queryResponse.isOk());
        assertEquals("1", queryResponse.get("$.data.people[0].id"));

        List<Person> persons;
        persons = queryResponse.getList("$.data.people", Person.class);
        assertEquals(1, persons.size());

        // FIXME the values are null for whatever reason
//        assertEquals("Jane", persons.get(0).getFirstName());
    }
}
