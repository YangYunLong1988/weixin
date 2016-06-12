var options = fis.get("options");
var version = options["child-flag"] || "v1.0.0";
var webapp = 'diana-web/src/main/webapp';
var online_domain = 'http://diana.static.jlfex.com/diana/' + version;
var test_domain = 'http://172.16.200.190/diana';

var no_release = {
    release: false
};

fis.set('project.files', [webapp + '/**']);
fis.set('settings.parser.node-sass', {
    include_paths: ['libs/compass/lib']
});
fis.set('settings.parser.sass', {
    include_paths: ['libs/compass/lib']
});

fis.match(webapp + '/vm/(*.html)', {
    release: '/templates/$1',
    rExt: '.vm'
});

fis.match(webapp + '/html/(*.html)', {
    release: '/static/html/$1'
});

fis.match(webapp + '/img/(**)', {
    release: '/static/img/$1',
    url: '/img/$1'
});

fis.match(webapp + '/js/(**)', {
    isMod: false,
    id: '$1',
    release: '/static/js/$1',
    url: '/js/$1'
});

fis.match(webapp + '/scss/(*.scss)', {
    parser: fis.plugin('sass'),
    rExt: '.css',
    release: '/static/css/$1',
    url: '/css/$1'
});

fis.match(webapp + '/scss/(_*.scss)', no_release);

fis.media("test")
    .match('*', {
        domain: test_domain
    })
    .match(webapp + '/scss/(*.scss)', {
        parser: fis.plugin('node-sass'),
    });

fis.media("online")
    .match('*', {
        domain: online_domain
    })
    .match(webapp + '/scss/(*.scss)', {
        parser: fis.plugin('node-sass'),
    });