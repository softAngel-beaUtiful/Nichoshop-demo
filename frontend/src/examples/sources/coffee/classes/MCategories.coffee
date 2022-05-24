class CategoryPath
  constructor: (@http) ->
    # categories path. Contains built view on real category
    @categories = [
      {id: 0,name: "Root Category"}
    ]
    # category last in current path but not in @categories, but from cache
    @currentCategory = @categories[0]

    # category children cache
    @children = []

    # children of category last in current path (@categories)
    @currentChildren = []

    @getChildren()

  getChildren: (id = 0) =>
    @currentChildren = []
    i = _.findIndex(@children, (o) =>
      o.id == id
    )
    if (i == -1)
      @http.get("/api/admin/category/children/" + id).
      success((data) =>
        if (data.children)
          data = data.children
        if (!_.isArray(data))
          data = []
        @children.splice @children.length, 0, {
          id: id
          children: angular.copy data
        }
        @getChildren(id)
      )
    else
      @currentChildren = @children[i].children

  chooseCategory: (id) ->
    i = _.findIndex(@categories, (o) ->
      o.id == id
    )
    @categories.splice i + 1, @categories.length
    @currentCategory = @categories[@categories.length - 1]
    @getChildren(@categories[i].id)

  chooseSubCategory: (id) ->
    i = _.findIndex(@currentChildren, (o) ->
      o.category.id == id
    )
    @currentCategory = @currentChildren[i].category
    @categories.push @currentCategory
    @getChildren(@currentChildren[i].category.id)

class CategoryCondition
  types: [
    {
      id: "condition1"
      name: "Condition Type 1"
      values: ["New"]
    }
    {
      id: "condition2"
      name: "Condition Type 2"
      values: ["New", "Used"]
    }
    {
      id: "condition3"
      name: "Condition Type 3"
      values: ["New with tags", "New without tags", "New with defects", "Pre-owned"]
    }
    {
      id: "condition4"
      name: "Condition Type 4"
      values: ["Brand New", "Like New", "Very Good", "Good", "Accessible"]
    }
    {
      id: "condition5"
      name: "Condition Type 5"
      values: ["New", "New other (see details)", "Used", "For parts or not working"]
    }
    {
      id: "condition6"
      name: "Condition Type 6"
      values: ["New", "Certified pre-owned", "Used"]
    }
    {
      id: "condition7"
      name: "Condition Type 7"
      values: ["New", "New other (see details)", "Remanufactured", "Used", "For parts or not working"]
    }
    {
      id: "condition8"
      name: "Condition Type 8"
      values: ["New", "New other (see details)", "Manufacturer refurbished", "Seller refurbished", "Used", "For parts or not working"]
    }
  ]


  constructor: () ->
    @data = ""

  open: (conditionType) =>
    @data = conditionType

  options: (dt) =>
    dt ?= @data
    if (dt && dt != "")
      (d.values for d in @types when d.id == dt)[0]
    else
      []

class CategoryList
  constructor: (@scope, @http, @uibModal) ->
    @c1 = new CategoryPath(@http)
    @cc = new CategoryCondition(@http)

    @c2 = {}

    @newCategory = {}

    @attributes = {}
    @attribute = {}

    @resetAttributes()
    @resetAttribute()
    @modal = false

  resetAttributes: () =>
    @attributes =
      categoryId: 0
      data: []
  resetAttribute: (attribute) =>
    if (attribute)
      @attribute =
        name: attribute.name
        attributeType: "ENUM"
        options: ({v: o} for o in attribute.valueOptions)
        id: attribute.id
        defaultValue: attribute.defaultValue
      if (!@attribute.defaultValue)
        @attribute.defaultValue = attribute.valueOptions[0]
    else
      @attribute =
        name: ""
        attributeType: "ENUM"
        options: [{v: ""}]

  getAttributes: (categoryId) =>
    @http {url: "/api/admin/category/#{categoryId}/attributes", method: "GET"}
    .success (data) =>
      @attributes =
        categoryId: categoryId
        data: (angular.copy data)
      console.log(JSON.stringify(@attributes))
    .error () =>
      @resetAttributes()
  removeAttribute: (id) =>
    categoryId = @c1.currentCategory.id

    @http {url: "/api/admin/category/#{categoryId}/attribute/#{id}", method: "DELETE"}
    .success () =>
      toastr.success "Attribute was deleted"
      @getAttributes(categoryId)
    .error () =>
      toastr.error "Attribute was not deleted"


  addOption: () =>
    @attribute.options.push {v: ""}
  removeOption: (index) =>
    @attribute.options.splice index, 1

  addAttributeOpenModal: (attribute = undefined) =>
    @resetAttribute(attribute)
    @modal = @uibModal.open {
      animation: true
      templateUrl: 'addAttribute.html'
      scope: @scope
      size: 'lg'
    }

  ok: () =>
    categoryId = @c1.currentCategory.id

    dataToSend = angular.copy @attribute

    console.log("Before modifications: #{JSON.stringify(dataToSend)}")
    if (dataToSend.options && dataToSend.options.length)
      dataToSend.options = (o.v for o in dataToSend.options)
    else
      dataToSend.options = []
    if (!dataToSend.defaultValue || dataToSend.defaultValue == "")
      dataToSend.defaultValue = dataToSend.options[0]

    console.log("Will send to server attribute: #{JSON.stringify(dataToSend)}")

    if (dataToSend.id)
      @http {url: "/api/admin/category/#{categoryId}/attribute/#{dataToSend.id}", method: "PUT", data: dataToSend}
      .success (data) =>
        toastr.success "Attribute #{@attribute.name} was created for '#{@c1.currentCategory.name}'"
        @getAttributes(categoryId)
        @cancel()
      .error () =>
        toastr.error "Attribute #{@attribute.name} was not created"
    else
      @http {url: "/api/admin/category/#{categoryId}/attributes", method: "POST", data: dataToSend}
      .success (data) =>
        toastr.success "Attribute #{@attribute.name} was created for '#{@c1.currentCategory.name}'"
        @getAttributes(categoryId)
        @cancel()
      .error () =>
        toastr.error "Attribute #{@attribute.name} was not created"

  cancel: () =>
    @modal.close()
    @modal = false

  assignConditionType: () =>
    categoryId = @c1.currentCategory.id

    console.log("Condition type data: #{JSON.stringify(@cc.data)}, options: #{@cc.options()}")
    @http {url: "/api/admin/category/#{categoryId}", method: "PUT", data: {conditionType: @cc.data}}
    .success () =>
      if (@data == "")
        toastr.success "Condition type was cleared"
      else
        toastr.success "Condition type was assigned"

      @c1.currentCategory.conditionType = @cc.data

      @cancel()
    .error () =>
      toastr.error "Condition type was not changed"


  assignConditionTypeOpenModal: () =>
    console.log("Will open assign condition type dialog: #{JSON.stringify(@c1.currentCategory)}")

    @cc.open(@c1.currentCategory.conditionType)
    @modal = @uibModal.open {
      animation: true
      templateUrl: 'assignConditionType.html'
      scope: @scope
      size: 'lg'
    }
  chooseAnotherParentModal: () =>
    @c2 = new CategoryPath(@http)
    @modal = @uibModal.open {
      animation: true
      templateUrl: 'chooseAnotherParent.html'
      scope: @scope
      size: 'lg'
    }

  chooseAnotherParentSubmit: () =>
    childId = @c1.currentCategory.id
    parentId = @c2.currentCategory.id
    console.log("Will put to " + "/api/admin/category/parent/#{parentId}/#{childId}")

    @http {url: "/api/admin/category/parent/#{parentId}/#{childId}", method: "PUT"}
    .success () =>
      toastr.success "Category was successfully moved to new parent"
      @c1 = new CategoryPath(@http)
      @cancel()
    .error () =>
      toastr.error "Category was not moved to new parent"

  addCategoryModal: () =>
    @newCategory =
      name: ""
    @modal = @uibModal.open {
      animation: true
      templateUrl: 'addCategory.html'
      scope: @scope
      size: 'lg'
    }
  addCategoryOk: () =>
    parentId = @c1.currentCategory.id
    @http {url: "/api/admin/category/parent/#{parentId}", method: "POST", data: {name: @newCategory.name}}
    .success () =>
      toastr.success "Category #{@newCategory.name} was created"
      @c1 = new CategoryPath(@http)
      @cancel()
    .error () =>
      toastr.error "Category was not moved to new parent"



  removeCategory: () =>
    childId = @c1.currentCategory.id
    parentId = -1
    @http {url: "/api/admin/category/parent/#{parentId}/#{childId}", method: "PUT"}
    .success () =>
      toastr.success "Category was successfully moved to trash"
      @c1 = new CategoryPath(@http)
    .error () =>
      toastr.error "Some error"





