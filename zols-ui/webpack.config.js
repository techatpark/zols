const fs = require('fs').promises;
const path = require('path');
const glob = require('glob');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

const proxyConfig = require('./proxy.config.js');

module.exports = {
    // Dynamically find all JS files in the src/js folder
    entry: glob.sync('./src/js/*.js').reduce((entries, file) => {
        const name = path.basename(file, '.js'); // Use filename as entry key
        entries[name] = file;
        return entries;
    }, {}),
    output: {
        filename: 'js/[name].js', // Output JS bundles under the `js/` folder in dist
        path: path.resolve(__dirname, 'dist'),
    },
    // plugins: [new CleanWebpackPlugin()],
    mode: 'production', // Change to 'production' for production builds
    devServer: {
      proxy: proxyConfig,
      static: {
        directory: path.join(__dirname, 'dist'),
      },
      port: 3000, // The port you want to use
      open: true,
      hot:true,
      liveReload: true,
      setupMiddlewares: (middlewares, devServer) => {
      
      if (!devServer) {
        throw new Error('webpack-dev-server is not defined');
      }

      middlewares.unshift({
        name: 'custom-headers',
        middleware: (req, res, next) => {
          res.setHeader('X-Content-Type-Options', 'nosniff');
          next();
        },
      });
    
      devServer.app.get(['/questions*', '/ta/questions*'], async (req, res) => {
        const content = await fs.readFile('./dist/practices/question/index.html', 'utf8');
        res.send(content);
      });

      devServer.app.get(['/quiz*', '/ta/quiz*'], async (req, res) => {
        const content = await fs.readFile('./dist/practices/quiz/index.html', 'utf8');
        res.send(content);
      });
    
      devServer.app.get('/profile*', async (req, res) => {
        const content = await fs.readFile('./dist/profile/index.html', 'utf8');
        res.send(content);
      });
    
      return middlewares;
    },       
    },
    performance: {
        maxEntrypointSize: 500000,
        maxAssetSize: 500000, // 1500 KB
    }
};
