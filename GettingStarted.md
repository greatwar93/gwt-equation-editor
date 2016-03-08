**All API and class hierarchies are subject to change !**
_Please, provide your ideas of better API._ :)

There is an example of an editor built over this library: **_Example.java_** (in _org.formed.client.example_ package).
You can get started by looking at it, playing with it and building your own version.


## GWT ##

There is an example of an editor built over this library: **_Example.java_** (in _org.formed.client.example_ package).
You can get started by looking at it, playing with it and building your own version.

If you want to use another **Canvas** implementation than we do, then follow the Java SE section instructions.


## Java SE ##

If you want to use this library within **Java SE** application you'll have to:
  * a) derive a new descendant from a _BaseDrawer_ class. Just look at _SurfaceDrawer_ class and do the same. There's not much work to be done - you have to provide line drawing, text measuring and rendering routines, etc.
  * b) re-write _Example.java_ to use your _Drawer_ implementation instead of _SurfaceDrawer_, and to use the UI-rendering and event handling of the Java SE GUI framework that you are using.


## Architecture ##

**Formulas (equations) are constructed with** _Formula_ objects and _FormulaItem_ implementations: _SimpleElement_, _OperatorElement_, _LeftCloser_, _RightCloser_, _DivisorElement_, _FunctionElement_, _RootElement_. Just have a look at the top of _Example.java_ code.
You might need other classes from _org.formed.client.formula.elements_ package (BaseElement, PoweredElement, PlaceElement) if you want to add your own types of items (elements).

**Editing capabilities are coded**:
  * mostly in _BaseDrawer_ abstract class where only platform independent code is present (_SurfaceDrawer_ is derived from it and adds actual rendering of primitives). If you want, you can code completely different _Drawer_ implementation.
  * the other part is in _Example.java_.

**Platform specific rendering code** is isolated in _SurfaceDrawer_ class that is derived from _BaseDrawer_ abstract class (where only platform independent code is present).

**_Cursor_ class is used** to specify the place where the cursor is positioned in a formula. You have to tackle with it only if you are redoing editor internals - _BaseDrawer_ etc. (but in that case you'll have it a lot).

**Undo/redo:** we use Command pattern for this. All commands are provided to _Undoer_ by the _Drawer_ that you use. But the origin of them is different. Some commands are constructed by _FormulaItem_ implementations and some are by _BaseDrawer_ implementation of _Drawer_.