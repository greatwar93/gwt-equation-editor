**Equation/Formula Editor for GWT** that preserves formula semantics.

Preserving formula semantics - what does that mean ?
In short - you can use entered formulas to make real calculations.
But there's more: PreservingFormulaSemantics. :)

**Current version: 0.0.7a** (repository sources and showcase)

**Cool implemented features:**
  * Code completion (for greek variables, function and operator names)
  * Entering formulas completely without a mouse (mouse is also supported)
  * Editor can be easily integrated into any GWT, Java SE or Java ME project (for Java you have to implement text and line rendering methods yourself - it is a matter of couple a minutes)

All most important features have been implemented. Few have left. It's now in refactoring and documenting phase. API suggestions and requests are welcome. Soon we'll move to beta phase.

**You now can see and try the showcase of the editor at:**
http://formula-editor.appspot.com/

**Implemented features:**
  * variables, numbers and other elements (infinity and other)
  * operators (+, -, `*`, <, >, =, less or equal, greater or equal and others), brackets
  * functions
  * editing via keyboard with **auto completion** (for greek variables, functions, operators and other elements)
  * editing via mouse
  * fractions (brackets are respected when entering fractions)
  * raising to powers
  * undo and redo actions
  * selection of formula parts and Cut/Copy/Paste
  * all unicode characters (composites are supported only via code completion)
  * some automatic hints

An example of editor usage (buttons added only to show how it might be done - you can do it cooler :) ):
![http://gwt-equation-editor.googlecode.com/files/formula-editor-0.0.7.a-screenshot.png](http://gwt-equation-editor.googlecode.com/files/formula-editor-0.0.7.a-screenshot.png)

**Features to be implemented:**
  * selected items dragging via mouse
  * automatic brackets insertion in functions arguments, when needed
  * rendering brackets of appropriate size
  * composite unicode characters (native support without coding-in auto-completion for them)
  * automatic hints
  * export formula text
  * indexes
  * limits, integral, differential operators
  * vectors, matrices and transposition
  * sets and sets operations

**If you want a feature** - we are open to requests, mockups and designs.

If you are interested in using such a library and widget in a project, you can contribute by reviewing my code and class hierarchy to make it better suit your needs.

Project requires:
> GWT 2.0
> GWT-g2d
> GWT-incubator

&lt;wiki:gadget url="http://www.ohloh.net/p/480920/widgets/project\_basic\_stats.xml" height="220" border="1"/&gt;

P.S. All the help is appreciated !
We are developing in a slow pace, cause it is a by product of our main work. Never the less it'll be done eventually. :) We are now evaluating internal design concepts for it. So, if you have any ideas, suggestions, feature requests, UI concepts or code popping of your head - c'mon, contribute! ;)

P.P.S. We are open and grateful even to the most silliest suggestions if they come from your kind heart and good will.

&lt;wiki:gadget url="http://www.ohloh.net/p/480920/widgets/project\_users.xml?style=blue" height="100" border="0"/&gt;