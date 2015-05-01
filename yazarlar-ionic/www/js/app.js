// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', ['ionic', 'starter.controllers', 'starter.services', 'ui.router'])

    .run(function ($ionicPlatform) {
        $ionicPlatform.ready(function () {
            // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
            // for form inputs)
            if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
                cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
            }
            if (window.StatusBar) {
                // org.apache.cordova.statusbar required
                StatusBar.styleLightContent();
            }
            if(window.plugins && window.plugins.AdMob) {
                var admob_key = "ca-app-pub-9258778632843530/5511466350";
                var admob = window.plugins.AdMob;
                admob.createBannerView(
                    {
                        'publisherId': admob_key,
                        'adSize': admob.AD_SIZE.BANNER,
                        'bannerAtTop': false
                    },
                    function() {
                        admob.requestAd(
                            { 'isTesting': false },
                            function() {
                                admob.showAd(true);
                            },
                            function() { console.log('failed to request ad'); }
                        );
                    },
                    function() { console.log('failed to create banner view'); }
                );
            }
        });
    })

    .config(function ($stateProvider, $urlRouterProvider) {
        // Ionic uses AngularUI Router which uses the concept of states
        // Learn more here: https://github.com/angular-ui/ui-router
        // Set up the various states which the app can be in.
        // Each state's controller can be found in controllers.js
        $stateProvider

            // setup an abstract state for the tabs directive
            //.state('tab', {
            //    url: "/tab",
            //    abstract: true,
            //    templateUrl: "templates/tabs.html"
            //})

            // Each tab has its own nav history stack:

            .state('newspapers', {
                url: '/newspapers',
                templateUrl: 'templates/newspapers.html',
                controller: 'NewspaperController'
            })
            .state('newspapers-detail', {
                url: '/newspapers/:newspaperId',
                templateUrl: 'templates/newspaper-detail.html',
                controller: 'NewspaperDetailController'
            })
            .state('author-detail', {
                url: '/authors/:authorId',
                templateUrl: 'templates/author-detail.html',
                controller: 'AuthorDetailController'
            })

            .state('article-detail', {
                url: '/articles/:articleId',
                templateUrl: 'templates/article-detail.html',
                controller: 'ArticleController'
            })
        ;

        // if none of the above states are matched, use this as the fallback
        $urlRouterProvider.otherwise('/newspapers');

    });
