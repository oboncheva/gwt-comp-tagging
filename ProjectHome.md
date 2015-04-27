## Description ##
Tagging is one of basic tools in Web 2.0. This project provides pure GWT implemented component for tagging with advanced usage.
  * Suggestion loading customized to GWT RPC with synchronization timestamps
  * There are three modes:
    * _READ_ - represents tags
    * _WRITE_ - stadard tagging. Write new tag into inputtext that is placed right after last tag, or choose tag from suggestions
    * _SELECT\_BOX_ - only suggested tags
  * Default theme
  * Custom presenters




## Architecture ##
Component is constructed from 3 parts:
  * HTML List where list items represents tags.
  * HTML inputText for inserting new tags.
  * Suggestion list


Users of component can:
  * theme input tag by its will
  * create own tag presenter or use default one
  * create own suggestion presenter or use default one
  * switch between modes in real time



## About ##
This project is part of GWT component family - **gwt-comp**. It has not to be a new component framework like SmartGWT or similar. Main reason is to offer single component for specific usage without need of downloading the whole framework - because size matters.

