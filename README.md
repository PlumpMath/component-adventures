# arriving at components

exploration of the when/where organizing code in components makes sense

goals:

* implement a little system that takes a google spreadsheet id, selected columns, and returns the rows as edn of selected columns.

* if handed a channel, poll the spreadsheet for some default interval.

* if handed a channel and an interval, poll the sheet for that amount of time