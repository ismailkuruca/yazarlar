angular.module('starter.controllers', [])

    .controller('AuthorController', function ($scope, Authors) {
        $scope.authors = Authors.all();
        $scope.remove = function (author) {
            Authors.remove(author);
        }
    })

    .controller('AuthorDetailController', function ($scope, $stateParams, Authors, Articles, $ionicLoading) {
        $ionicLoading.show();
        $scope.author = Authors.get($stateParams.authorId);
        var articles = Articles.getByAuthorId($stateParams.authorId);
        articles.then(function(response) {
            $scope.articles = response;
            $ionicLoading.hide();
        }, function(error) {
            console.log("AUTHOR_DETAIL_CONTROLLER PROMISE ERROR!!!");
            $ionicLoading.hide();
        })

    })

    .controller('NewspaperController', function ($scope, Newspapers) {
        var newspapers = Newspapers.all();
        newspapers.then(function(response) {
            $scope.newspapers = response;
        }, function(error) {
            console.log("NEWSPAPER_CONTROLLER PROMISE ERROR!!!");
        })
    })

    .controller('NewspaperDetailController', function ($scope, $stateParams, Authors, Newspapers, $ionicLoading) {
        $ionicLoading.show();
        $scope.newspaper = Newspapers.get($stateParams.newspaperId);
        var authors = Authors.getAuthorsByNewspaperId($stateParams.newspaperId);
        authors.then(function(response) {
            $scope.authors = response;
            $ionicLoading.hide();
        }, function(error) {
            console.log("NEWSPAPER_DETAIL_CONTROLLER PROMISE ERROR!!!");
            $ionicLoading.hide();
        })
    })

    .controller('ArticleController', function ($scope, $stateParams, Articles) {
        $scope.article = Articles.get($stateParams.articleId);
    });
