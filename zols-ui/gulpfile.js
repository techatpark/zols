'use strict';
// generated on 2015-10-03 using generator-gulp-bootstrap 0.0.4

var gulp = require('gulp');
var browserSync = require('browser-sync');
var reload = browserSync.reload;
var gutil = require('gulp-util');

// load plugins
var $ = require('gulp-load-plugins')();

gulp.task('styles', function () {
    return gulp.src('app/styles/main.scss')
            .pipe($.sass({errLogToConsole: true}))
            .pipe($.autoprefixer('last 1 version'))
            .pipe(gulp.dest('app/styles'))
            .pipe(reload({stream: true}))
            .pipe($.size())
            .pipe($.notify("Compilation complete."));
    ;
});

gulp.task('scripts', function () {
    return gulp.src('app/scripts/**/*.js')
            .pipe($.jshint())
            .pipe($.jshint.reporter(require('jshint-stylish')))
            .pipe($.size());
});

gulp.task('html', ['styles', 'scripts'], function () {
    var jsFilter = $.filter('**/*.js');
    var cssFilter = $.filter('**/*.css');

    return gulp.src(['app/**/*.html','!app/bower_components/**'])
            .pipe($.useref.assets())
            .pipe(jsFilter)
            .pipe($.uglify())
            .pipe(jsFilter.restore())
            .pipe(cssFilter)
            .pipe($.csso())
            .pipe(cssFilter.restore())
            .pipe($.useref.restore())
            .pipe($.useref())
            .pipe(gulp.dest('dist'))
            .pipe($.size());
});

gulp.task('images', function () {
    return gulp.src('app/images/**/*')
            .pipe($.imagemin({
                optimizationLevel: 3,
                progressive: true,
                interlaced: true
            }))
            .pipe(gulp.dest('dist/images'))
            .pipe(reload({stream: true, once: true}))
            .pipe($.size());
});

gulp.task('fonts', function () {
    var streamqueue = require('streamqueue');
    return streamqueue({objectMode: true},
    $.bowerFiles(),
            gulp.src('app/fonts/**/*')
            )
            .pipe($.filter('**/*.{eot,svg,ttf,woff}'))
            .pipe($.flatten())
            .pipe(gulp.dest('dist/fonts'))
            .pipe($.size());
});

gulp.task('clean', function () {
    return gulp.src(['app/styles/main.css', 'dist','../zols-cms-plugin/src/main/resources/static','../zols-cms-plugin/src/main/resources/templates'], {read: false}).pipe($.clean({force: true}));
});

gulp.task('build', ['html', 'images', 'fonts']);

gulp.task('default', ['clean'], function () {
    gulp.start('build');
    gulp.start('templates');
});

gulp.task('serve', ['styles'], function () {
    browserSync.init(null, {
        server: {
            baseDir: 'app',
            directory: true
        },
        debugInfo: false,
        open: false,
    }, function (err, bs) {
        require('opn')(bs.options.get('urls').get('local'));
        console.log('Started connect web server on ' + bs.options.url);
    });
});

// inject bower components
gulp.task('wiredep', function () {
    var wiredep = require('wiredep').stream;
    gulp.src('app/styles/*.scss')
            .pipe(wiredep({
                directory: 'app/bower_components'
            }))
            .pipe(gulp.dest('app/styles'));
    gulp.src('app/**/*.html')
            .pipe(wiredep({
                directory: 'app/bower_components',
                exclude: ['bootstrap-sass-official']
            }))
            .pipe(gulp.dest('app'));
});

gulp.task('watch', ['serve'], function () {

    // watch for changes
    gulp.watch(['app/**/*.html'], reload);
    gulp.watch('app/styles/**/*.scss', ['styles']);
    gulp.watch('app/scripts/**/*.js', ['scripts']);
    gulp.watch('app/images/**/*', ['images']);
    gulp.watch('bower.json', ['wiredep']);
});

var replace = require('gulp-replace');

gulp.task('templates', ['build'], function () {    
	gulp.src(['dist/**/*.html'])
    		.pipe(replace(/script src="(.*)"/g, 'script src="$1" th:src="@{$1}"'))
            .pipe(replace(/href="(.*).css"/g, 'href="$1.css" th:href="@{$1.css}"'))
            .pipe(replace('.css}">', '.css}"/>'))
            .pipe(replace('@{scripts/', '@{/scripts/'))
            .pipe(replace('@{styles/', '@{/styles/'))
            .pipe(gulp.dest('../zols-cms-plugin/src/main/resources/templates'));

    gulp.src(
            ['dist/**/*.js'
                        , 'dist/**/*.css'
                        , 'dist/**/*.png'
                        , 'dist/**/*.svg'
                        , 'dist/**/*.eot'
                        , 'dist/**/*.ttf'
                        , 'dist/**/*.woff'])
            .pipe(gulp.dest('../zols-cms-plugin/src/main/resources/static'));

    gulp.src(
            ['app/styles/sceeditor/**'])
            .pipe(gulp.dest('../zols-cms-plugin/src/main/resources/static/styles/sceeditor'));
    
});
