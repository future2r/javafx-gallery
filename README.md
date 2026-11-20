# JavaFX Gallery

A desktop application that showcases the standard controls of JavaFX – inspired by the purpose of the
[WinUI Gallery](https://github.com/microsoft/WinUI-Gallery), but built with Java, JavaFX and Maven and styled with
JavaFX's own Modena theme.

Every page demonstrates one control with live examples, an options panel that changes the example on the fly, and a
link to the official API documentation.

## Features

- **69 demo pages in 12 categories**
  - **Basic input** – Button, ToggleButton, CheckBox, RadioButton, Hyperlink, Slider, Spinner, ColorPicker,
    DatePicker, ComboBox, ChoiceBox, MenuButton, SplitMenuButton
  - **Text** – Label, TextField, PasswordField, TextArea, HTMLEditor
  - **Collections** – ListView, TableView, TreeView, TreeTableView
  - **Layout** – HBox/VBox, GridPane, BorderPane, StackPane, FlowPane, TilePane, AnchorPane, SplitPane, ScrollPane,
    TitledPane, Accordion, TabPane
  - **Menus & toolbars** – MenuBar, ContextMenu, ToolBar
  - **Dialogs & flyouts** – Alert, TextInputDialog, ChoiceDialog, Dialog, Tooltip, Popup, FileChooser/DirectoryChooser
  - **Status & info** – ProgressBar, ProgressIndicator, Separator
  - **Navigation** – Pagination, TabPane
  - **Media & web** – ImageView, MediaView, WebView
  - **Charts** – LineChart, AreaChart, BarChart, StackedBarChart, PieChart, ScatterChart, BubbleChart
  - **Shapes & graphics** – 2D shapes, Canvas, 3D graphics
  - **Styling & motion** – Effects, Transitions, Timeline, CSS styling
- **Navigation** – a tree with all categories and pages, a search field, a home page and category pages with tiles,
  and a back button with history
- **Themes** – light, dark (Modena based) or following the operating system setting
- **Remembered settings** – theme, window position and size, and the last visited page are stored with
  `java.util.prefs`

## Requirements

- JDK 25
- No Maven installation needed – the project comes with the Maven Wrapper

JavaFX 25 is pulled from Maven Central as a dependency.

## Getting started

Start the application:

```bash
./mvnw javafx:run
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

Build and run the tests:

```bash
./mvnw verify
```

Create a runtime image with `jlink` (output in `target/javafx-gallery`):

```bash
./mvnw javafx:jlink
```

The image contains its own Java runtime and is started with `target/javafx-gallery/bin/javafx-gallery`
(`javafx-gallery.bat` on Windows).

### Keyboard shortcuts

| Shortcut | Action |
|---|---|
| `Ctrl+F` or `Ctrl+E` | Focus the search field |
| `Enter` in the search field | Open the first matching page |
| `Esc` in the search field | Clear the search |
| `Alt+Left` or mouse back button | Go back |

## Project structure

The application is a Java module (`name.ulbricht.javafx.gallery`).

```
src/main/java/name/ulbricht/javafx/gallery/
├── GalleryApp.java       application entry point
├── catalog/              Category, ControlPage and GalleryCatalog – the list of all pages
├── shell/                main window, navigation, home/category/settings pages, DemoPage and Example
├── theme/                light/dark/system theme handling
├── settings/             persisted user settings
└── pages/<category>/     one controller per demo page, plus sample data in pages/data

src/main/resources/name/ulbricht/javafx/gallery/
├── shell/                FXML of the main window and the overview pages
├── pages/<category>/     one FXML file per demo page
├── css/                  gallery.css and dark.css
├── icons/                application icon in several sizes
├── media/                example video for the MediaView page
└── web/                  sample HTML page for the WebView page
```

## Adding a page

1. Create the FXML file under `src/main/resources/name/ulbricht/javafx/gallery/pages/<category>/`. Use `DemoPage` as
   the root element and add one or more `Example` elements:

   ```xml
   <DemoPage xmlns="http://javafx.com/javafx" xmlns:fx="http://javafx.com/fxml"
             fx:controller="name.ulbricht.javafx.gallery.pages.basicinput.MyControlController">
       <Example title="A simple example">
           <Button fx:id="button" text="Click me"/>
           <options>
               <VBox spacing="10">
                   <CheckBox fx:id="disable" text="Disable"/>
               </VBox>
           </options>
       </Example>
   </DemoPage>
   ```

2. Create the controller in the package `name.ulbricht.javafx.gallery.pages.<category>`. If the page starts
   animations, media players or background tasks, implement `Disposable` to stop them when the user leaves the page.
3. Register the page in `GalleryCatalog` with its id, title, category, description and the documented API class.
   The page header (title, description, documentation link) is filled from this entry.

The smoke test loads every page of the catalog, so a new page is tested automatically.

## Tests

`PageSmokeTest` loads every page with its controller and stylesheets, checks the catalog, and navigates through the
overview pages in every theme.

To review the pages visually, the test can render each page to a PNG file, in the light and in the dark theme:

```bash
./mvnw verify -Dgallery.snapshots=target/snapshots
```
