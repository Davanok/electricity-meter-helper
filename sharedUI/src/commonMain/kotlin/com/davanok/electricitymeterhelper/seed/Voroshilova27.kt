package com.davanok.electricitymeterhelper.seed

import com.davanok.electricitymeterhelper.domain.Apartment

val Voroshilova27: List<Apartment> = buildList {
    add(Apartment("Общ. 1", "Общий"))
    addAll(List(20) { Apartment("Кв. ${it + 1}", "") })

    add(Apartment("Общ. 2", "Общий"))
    addAll(List(15) { Apartment("Кв. ${it + 21}", "") })

    add(Apartment("Общ. 3", "Общий"))
    addAll(List(14) { Apartment("Кв. ${it + 36}", "") })

    add(Apartment("Общ. 1", "Общий"))
    addAll(List(20) { Apartment("Кв. ${it + 50}", "") })
}