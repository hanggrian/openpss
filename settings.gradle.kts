/**
 * Main JavaFX application module.
 * Be sure to turn off build's debug when building executable jar.
 */
include("app")

/**
 * In order for SceneBuilder to render fxml found in app module, custom library must be imported in SceneBuilder.
 * This module is written exactly for that purpose.
 * Controls and layouts that are not in fxml (programmatic) should not be in this module in favor of smaller non-executable jar size.
 */
include("scene")