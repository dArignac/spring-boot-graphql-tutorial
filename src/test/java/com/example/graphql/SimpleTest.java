package com.example.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTest;
import com.graphql.spring.boot.test.GraphQLTestTemplate;

// see https://github.com/graphql-java-kickstart/graphql-spring-boot/blob/master/example-graphql-tools/src/test/java/com/graphql/sample/boot/GraphQLToolsSampleApplicationTest.java
@ExtendWith(SpringExtension.class)
//@GraphQLTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;

    @Test
    public void testHello() throws IOException {
        GraphQLResponse response = graphQLTestTemplate.postForResource("query.graphql");
        assertNotNull(response);
        assertTrue(response.isOk());
        assertNull(response.getList("$.data", Object.class));
    }

    @Test
    public void createPeople() throws IOException {
        ObjectNode variables = new ObjectMapper().createObjectNode();
        variables.put("firstName", "Jane");
        variables.put("lastName", "Doe");
        GraphQLResponse response = graphQLTestTemplate.perform("create-person.graphql", variables);
        assertNotNull(response);
        assertEquals("Jane", response.get("$.data.createPerson.firstName"));
        assertEquals("Doe", response.get("$.data.createPerson.lastName"));
        assertEquals("Jane Doe", response.get("$.data.createPerson.fullName"));
        assertNull(response.get("$.data.createPerson.dateOfBirth"));
    }
}
