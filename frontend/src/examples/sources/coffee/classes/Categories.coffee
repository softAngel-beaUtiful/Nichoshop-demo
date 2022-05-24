class Categories
  data: {}
  lastId: false

  constructor: (@scope, @http, @location) ->
    s = @scope
    @http.get("/api/category/sidebar2").
    success((data) ->
#        console.log(JSON.stringify(data))
      s.sidebar.data = data
    )
  showProducts: (categoryId) ->
    @scope.products.setCategoryId(categoryId)

  setCategory: (categoryId) ->
    @location.path("/").replace()
    @location.search("filters", "category:#{categoryId}")
