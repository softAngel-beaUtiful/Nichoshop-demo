class Money
  @currencies:
    EURO:
      id: 978
      name: "Euro"
      sign: '\u20AC'
    DOLLAR:
      id: 840
      name: "US Dollar"
      sign: '$'
  @fromDecimal: (value) =>
    new Money(Math.floor(value * 100))

  constructor: (@amount, @base = 100, @currency = Money.currencies.EURO) ->
    @high = Math.floor(@amount / @base)
    @low = @amount % @base
  multiply: (qty) =>
    new Money(@amount * Math.floor(qty))
  asString: (delim = ".") =>
    "#{@currency.sign}#{@high}#{delim}#{if (@low > 9) then @low else "0#{@low}"}"
