/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.datastore.query;

import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.zols.datastore.query.MapQuery.*;

/**
 *
 * @author sathish
 */
public class MapQueryTest {

    @Test
    public void testMapQuery() {

        List<Map<String, Object>> people = new ArrayList();

        Map<String, Object> person, city;

        person = new HashMap<>();
        person.put("name", "Sathish");
        person.put("age", 36);
        city = new HashMap<>();
        city.put("name", "Madurai");
        city.put("pin", 625000);
        person.put("city", city);
        people.add(person);

        person = new HashMap<>();
        person.put("name", "Anand");
        person.put("age", 38);
        city = new HashMap<>();
        city.put("name", "Madurai");
        city.put("pin", 625000);
        person.put("city", city);
        people.add(person);

        person = new HashMap<>();
        person.put("name", "Marees");
        person.put("age", 26);
        city = new HashMap<>();
        city.put("name", "Sivakaci");
        city.put("pin", 625001);
        person.put("city", city);
        people.add(person);

        Condition<MapQuery> condition = stringFiled("city.name").eq("Madurai");
        Predicate<Map<String, Object>> predicate = condition.query(new PredicateVisitor<>());
        assertEquals(2L, people.stream().filter(predicate).count(), "Retrieving Inner Primitive Query");

        condition = stringFiled("city.name").eq("Madurai").and().string("name").eq("Sathish");
        predicate = condition.query(new PredicateVisitor<>());
        assertEquals(1L, people.stream().filter(predicate).count(), "Retrieving Inner Primitive Query");

    }

}
