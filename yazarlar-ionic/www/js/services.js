angular.module('starter.services', [])

    .service('Authors', ['$http', '$q', function ($http, $q) {
        var authors = [];
        var last_request_failed = true;
        var promise = undefined;

        return {
            getAuthorsByNewspaperId: function (newspaperId) {
                promise = $http.get('http://188.166.97.48:8080/yazarlar/getAuthorsByNewspaperId' + '?id=' + newspaperId, {cache: true}).then(
                    function (response) {
                        last_request_failed = false;
                        authors = response.data;
                        return authors;
                    }, function (response) {  // error
                        last_request_failed = true;
                        return $q.reject(response);
                    });
                return promise;
            },
            get: function (authorId) {
                for (var i = 0; i < authors.length; i++) {
                    if (authors[i].id === parseInt(authorId)) {
                        return authors[i];
                    }
                }
                return null;
            }
        };
    }])

    .service('Newspapers', ['$http', '$q', function ($http, $q) {
        var newspapers = [];
        var last_request_failed = true;
        var promise = undefined;

        return {
            all: function () {
                if (!promise || last_request_failed) {
                    promise = $http.get('http://188.166.97.48:8080/yazarlar/getNewspapers', {cache: true}).then(
                        function (response) {
                            last_request_failed = false;
                            newspapers = response.data;
                            return newspapers;
                        }, function (response) {  // error
                            last_request_failed = true;
                            return $q.reject(response);
                        });
                }
                return promise;
            },
            get: function (newspaperId) {
                for (var i = 0; i < newspapers.length; i++) {
                    if (newspapers[i].id === parseInt(newspaperId)) {
                        return newspapers[i];
                    }
                }
                return null;
            }
        };
    }])

    .service('Articles', ['$http', '$q', function ($http, $q) {
        var articles = [];
        var last_request_failed = true;
        var promise = undefined;

        return {
            getByAuthorId: function (authorId) {
                promise = $http.get('http://188.166.97.48:8080/yazarlar/getArticlesByAuthorId' + '?id=' + authorId).then(
                    function (response) {
                        last_request_failed = false;
                        articles = response.data;
                        return articles;
                    }, function (response) {  // error
                        last_request_failed = true;
                        return $q.reject(response);
                    });
                return promise;
            },
            getMostPopular: function () {
                promise = $http.get('http://188.166.97.48:8080/yazarlar/getMostPopular').then(
                    function (response) {
                        articles = response.data;
                        return response.data;
                    }, function (response) {
                        return $q.reject(response);
                    });
                return promise;
            },
            getMostRecent: function () {
                promise = $http.get('http://188.166.97.48:8080/yazarlar/getMostRecent').then(
                    function (response) {
                        articles = response.data;
                        return response.data;
                    }, function (response) {
                        return $q.reject(response);
                    });
                return promise;
            },
            get: function (articleId) {
                for (var i = 0; i < articles.length; i++) {
                    if (articles[i].id === parseInt(articleId)) {
                        $http.get('http://188.166.97.48:8080/yazarlar/getArticlesById' + '?aid=' + articles[i].author.id + '&id=' + articles[i].id);
                        return articles[i];
                    }
                }
                return null;
            }
        };
    }]);
