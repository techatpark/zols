export default [
  {
    input: 'src/js/index.js',
    output: {
      file: 'dist/js/index.js',
      format: 'cjs'
    }
  },
  {
    input: 'src/js/core.js',
    output: {
      file: 'dist/js/core.js',
      format: 'cjs'
    }
  },
  {
  input: 'src/js/pages/schema.js',
  output: {
    file: 'dist/js/pages/schema.js',
    format: 'cjs'
  }
},
{
  input: 'src/js/pages/user.js',
  output: {
    file: 'dist/js/pages/user.js',
    format: 'cjs'
  }
},{
  input: 'src/js/pages/workspace.js',
  output: {
    file: 'dist/js/pages/workspace.js',
    format: 'cjs'
  }
},
{
  input: 'src/js/pages/data-warehouse.js',
  output: {
    file: 'dist/js/pages/data-warehouse.js',
    format: 'cjs'
  }
}
];

