lsApp = angular.module('lsApp', []);

lsApp.controller('SearchController', ["$http", function($http) {
        var searchController = this;

        searchController.query = "";
        searchController.made = false;

        searchController.results = [];

        searchController.doSearch = function() {
            $http.get('data.json?q=' + searchController.query).then(function(response) {
                searchController.results = response.data;
            }, function(response) {
                searchController.results = [];
            });
        };

        searchController.doSearch();
    }]);