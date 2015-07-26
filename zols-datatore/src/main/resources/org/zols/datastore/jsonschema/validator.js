function validateJsonSchema(schema, data) {
    var validate = jsen(schema);
    if (!validate(data)) {
        return validate.errors;
    }
    return null;         // true
}