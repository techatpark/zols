package org.zols.datastore.validator;

import org.zols.datastore.validator.Validator;
import org.zols.datastore.validator.ValidatedObject;
import java.io.IOException;
import java.util.Map;
import org.zols.datastore.domain.Category;
import org.zols.datastore.util.JSONUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author praveen
 */
public class ValidatorTest {

    private final Validator validator;

    private final JSONUtil jSONUtil;

    public ValidatorTest() {
        validator = new Validator();
        jSONUtil = new JSONUtil();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testGetObjectFromPOJO() throws IOException {
        System.out.println("\nPOJO Based");
        Map<String, Object> stringDataObject = jSONUtil.getJsonStringObject("category");
        ValidatedObject validatedObject = validator.getObject(Category.class, stringDataObject);
        System.out.println("Schema " + jSONUtil.getAsJsonString(validatedObject.getJsonSchema()));
        System.out.println("stringDataObject " + jSONUtil.getAsJsonString(stringDataObject));
        System.out.println("validatedObject " + jSONUtil.getAsJsonString(validatedObject.getDataObject()));
    }

    

}
