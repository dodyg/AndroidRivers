package com.silverkeytech.android_rivers.riverjs


fun sortRiverItemMeta(newsItems : List<RiverItemMeta>) : List<RiverItemMeta>{
    var sortedNewsItems = newsItems.filter { x -> x.item.isPublicationDate()!! }.sort(
            comparator {(p1: RiverItemMeta, p2: RiverItemMeta) ->
                val date1 = p1.item.getPublicationDate()
                val date2 = p2.item.getPublicationDate()

                if (date1 != null && date2 != null) {
                    date1.compareTo(date2) * -1
                } else if (date1 == null && date2 == null) {
                    0
                } else if (date1 == null) {
                    -1
                } else {
                    1
                }
            })

    return sortedNewsItems
}
