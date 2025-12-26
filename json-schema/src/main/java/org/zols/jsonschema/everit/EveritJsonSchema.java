/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zols.jsonschema.everit;

import com.github.erosb.jsonsKema.Schema;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.jsonsKema.SchemaLoaderConfig;
import com.github.erosb.jsonsKema.ValidationFailure;
import com.github.erosb.jsonsKema.Validator;
import com.github.erosb.jsonsKema.ValidatorConfig;
import com.github.erosb.jsonsKema.FormatValidationPolicy;
import com.github.erosb.jsonsKema.JsonValue;
import com.github.erosb.jsonsKema.JsonParser;

import org.json.JSONObject;
import org.zols.jsonschema.JsonSchema;
import org.zols.jsonschema.violations.JsonSchemaConstraintViolation;

import javax.validation.ConstraintViolation;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import java.util.function.Function;

import static com.github.erosb.jsonsKema.SchemaLoaderKt.createDefaultConfig;
import static java.util.stream.Collectors.toSet;

/**
 * The type Everit json schema.
 *
 * @author sathish
 */
public class EveritJsonSchema extends JsonSchema {

    /**
     * To cache a schema.
     */
    private final Schema schema;
    /**
     * To cache a Shemastreams.
     */
    private final Map<String, InputStream> schemaStreams;

    /**
     * Instantiates a new Everit json schema.
     *
     * @param schemaMap      the schema map
     * @param schemaSupplier the schema supplier
     */
    public EveritJsonSchema(final Map<String, Object> schemaMap,
                            final Function<String, Map<String, Object>>
                                    schemaSupplier) {
        super(schemaMap, schemaSupplier);
        schemaStreams = new HashMap<>();
        schema = new SchemaLoader(getJsonValue(schemaMap),
                getSchemaLoaderConfig()).load();



        //        schema = SchemaLoader.load(new JSONObject(schemaMap),
//                this::getSchemaInputStream);
    }

    /**
     * Instantiates a new Everit json schema.
     *
     * @param schemaId       the schema id
     * @param schemaSupplier the schema supplier
     */
    public EveritJsonSchema(final String schemaId,
                            final Function<String, Map<String, Object>>
                                    schemaSupplier) {
        super(schemaId, schemaSupplier);
        schemaStreams = new HashMap<>();
        schema = new SchemaLoader(getJsonValue(getSchemaMap()),
                getSchemaLoaderConfig())
                .load();
    }

    private InputStream getSchemaInputStream(final String schemaId) {
        InputStream inputStream = schemaStreams.get(schemaId);
        if (inputStream == null) {
            inputStream = new ByteArrayInputStream(
                    new JSONObject(
                            getSchemaSupplier().apply(schemaId)).toString()
                            .getBytes());
            schemaStreams.put(schemaId, inputStream);
        }
        return inputStream;
    }

    /**
     * validate method.
     *
     * @param jsonData the json data
     * @return
     */
    @Override
    public Set<ConstraintViolation> validate(
            final Map<String, Object> jsonData) {

        // create a validator instance for each validation (one-time use object)
        Validator validator = Validator.create(schema,
                new ValidatorConfig(FormatValidationPolicy.ALWAYS));

        ValidationFailure failure = validator.validate(getJsonValue(jsonData));

        if (failure != null) {
            if (failure.getCauses().isEmpty()) {
                Set<ConstraintViolation> constraintViolations =
                        new HashSet<>();
                constraintViolations.add(getConstraintViolation(failure));
                return constraintViolations;
            } else {
                return failure.getCauses().stream()
                        .map(this::getConstraintViolation)
                        .collect(toSet());
            }
        }


        return new HashSet<>();
    }

    /**
     * getConstraintViolation method.
     *
     * @param ve
     * @return null.
     */
    private JsonSchemaConstraintViolation getConstraintViolation(
            final ValidationFailure ve) {
        return null;

    }

    /**
     * asString method.
     *
     * @return string
     */
    @Override
    protected String asString() {
        return schema.toString();
    }

    private JsonValue getJsonValue(final Map<String, Object> jsonMap) {
        return new JsonParser(new JSONObject(jsonMap).toString())
                .parse();

    }

    private SchemaLoaderConfig getSchemaLoaderConfig() {
        SchemaLoaderConfig config = createDefaultConfig(new HashMap<>());
        // Creating a SchemaLoader config with a pre-registered schema by URI
        try {
            List<String> references = getReferences(getSchemaMap());

            Map<URI, String> schemaEntries = new HashMap<>();

            for (String reference: references) {
                schemaEntries.put(new URI(reference),

                        // then it will resolve it to this schema json
                        new JSONObject(this.getSchemaSupplier()
                                .apply(reference))
                                .toString());
            }

            config = createDefaultConfig(schemaEntries);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return config;
    }



}
