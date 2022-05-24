class Utils

  @extractFilters: (filters, filterName) =>
    res = []
    if (filters)
      parts = filters.split("~")
      filter = part for part in parts when part.startsWith("#{filterName}:")
      if (filter)
        value = filter.substring(filterName.length + 1)
        res = value.split(",")

    res