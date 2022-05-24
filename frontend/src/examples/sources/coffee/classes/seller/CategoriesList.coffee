class DnDCategoriesList
  data: {}

  constructor: (@scope, @http, @timeout) ->
    @http.get("/api/admin/category/all")
    .success (data) =>
      @scope.categories.data = angular.copy(data)
#      @scope.log(@data)
    .error (data) =>
      toastr.error("Something bad happens..")
