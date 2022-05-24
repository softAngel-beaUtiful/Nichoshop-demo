nichoshop = angular.module('nichoshopApp', [])

nichoshop.directive 'clickPrevent', () ->
  (scope, element, attrs) ->
    element.on('click', (e) ->
      console.log('click prevented')
      e.preventDefault()
      e.stopPropagation()
      return
    )

