class Product
  categories: [
    {
      id: 0
      title: "Root Category"
    }
  ]
  children: []

  currentChildren: []

  constructor: (@scope, @http, @uibModal) ->
    @data = {}
    @reset()

    @getChildren()
    @modal = false

  reset: () =>
    @data = {
      title: ""
      description: ""
      price: ""
      amount: ""
      condition: "NEW"
    }

  chooseCategory: (id) ->
    i = _.findIndex(@categories, (o) ->
      o.id == id
    )
    @categories.splice i + 1, @categories.length
    @getChildren(@categories[i].id)

  getChildren: (id = 0) ->
    @currentChildren = []
    i = _.findIndex(@children, (o) ->
      o.id == id
    )
    if (i == -1)
      s = @scope
      @http.get("/api/admin/category/children/" + id).
      success((data) ->
        if (data.children)
          data = data.children
        if (!_.isArray(data))
          data = []
#        console.log('got children: ' + JSON.stringify(data))
        s.product.children.splice s.product.children.length, 0, {
          id: id
          children: angular.copy data
        }
#        console.log(JSON.stringify(s.product.children))
        s.product.getChildren(id)
      )
    else
      @currentChildren = @children[i].children

  chooseSubCategory: (id) ->
    i = _.findIndex(@currentChildren, (o) ->
      o.category.id == id
    )

    @categories.splice @categories.length, 0, {
      id: @currentChildren[i].category.id
      title: @currentChildren[i].category.name
    }

    @getChildren(@currentChildren[i].category.id)

  createProduct: () ->
    submitData = angular.copy(@data)
    submitData.categoryId = @categories[@categories.length - 1].id
    submitData.price = Math.ceil(submitData.price * 100)

    console.log(JSON.stringify(submitData))
    @http.post("/api/admin/inventory/list", submitData).
    success (data) ->
      toastr.success("Product was created!")
    .error () ->
      toastr.error("Product was not created!")

  openModal: () =>
    @getChildren()

    @modal = @uibModal.open {
      animation: true
      templateUrl: 'createProduct.html'
      scope: @scope
      size: 'lg'
    }

  ok: () =>
    @createProduct()
    @close()

  close: () =>
    @modal.close()
    @modal = false



