package com.procrastimax.birthdaybuddy.models

/**
 * SortIdentifier is an interface which is used by enum classes from EventDay and its inherited subclasses
 * This Identifier is used to know to which attribute the event map has to be sorted (see EventHandler)
 */
interface SortIdentifier {
    fun Identifier(): Int
}