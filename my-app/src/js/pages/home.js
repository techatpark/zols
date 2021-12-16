
class Home {
  constructor() {
    this.setup();
  }

  setup() {
    
    fetch('/api/schema')
      .then(response => response.json())
      .then(schemas => {
        this.schemas = schemas;
        this.setSelectedSchema(schemas[0]);
      })
      .catch(err => {
        console.error(err);
      });
  }

  setSelectedSchema(schema) {
    const schemaList = document.getElementById('schemaList');
    document.getElementById('schemaMenuLink').innerHTML = schema.title;
    schemaList.innerHTML = '';
    this.schema = schema;
    for (var i = 0; i < this.schemas.length; i++) {
      if (this.schemas[i]['$ref'] == null
        && this.schemas[i] != schema) {
        var li = document.createElement('li');
        var link = document.createElement('a');
        link.classList.add('dropdown-item');
        link.innerHTML = (this.schemas[i].title);
        link.href = 'javascript://';
        li.appendChild(link);
        schemaList.appendChild(li);
      }
    }
  }

}
new Home()
