package com.wijayaprinting.manager.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class provides a method to print the content of a {@link TableView}. It
 * is at an early stage and currently doesn't support advanced CellFactorys.
 */
public class TreeTablePrinter {
    /**
     * Prints the content of the provided {@link TableView}.
     *
     * @param tableView See description.
     * @param jobArg    The {@link PrinterJob} to use. When the value is
     *                  <code>null</code> this method creates a default
     *                  {@link PrinterJob} and ends it. When a valid
     *                  {@link PrinterJob} is provided, the caller must close it.
     */
    public static <T> void print(TreeTableView<T> tableView, PrinterJob jobArg) {
        boolean createJob = jobArg == null;
        PrinterJob job;
        if (createJob) {
            job = PrinterJob.createPrinterJob();
        } else {
            job = jobArg;
        }
        printWithJob(tableView, job);
        if (createJob) {
            job.endJob();
        }
    }

    /**
     * The entry method for printing the table contents where {@link PrinterJob}
     * is guaranteed to not be <code>null</code>.
     *
     * @param tableView See description.
     * @param job       See description.
     */
    private static <T> void printWithJob(TreeTableView<T> tableView, PrinterJob job) {
        TreeTableView<T> copyView = createTableCopy(tableView, job);
        ArrayList<T> itemList = new ArrayList<>((Collection<T>) tableView.getRoot().getChildren());
        while (itemList.size() > 0) {
            printPage(job, copyView, itemList);
        }
    }

    /**
     * Prints the first n-th items of the list with the help of the provided
     * {@link TableView}. The concrete number of items that will be printed is
     * the maximum of items that you can add to the table so that the vertical
     * scrollbar of the table will not be visible.<br>
     * All printed items are removed of the given list.
     *
     * @param job      The job used for printing.
     * @param copyView See description.
     * @param itemList See description.
     */
    private static <T> void printPage(PrinterJob job, TreeTableView<T> copyView, ArrayList<T> itemList) {
        ScrollBar verticalScrollbar = getVerticalScrollbar(copyView);
        ObservableList<T> batchItemList = FXCollections.observableArrayList();
        copyView.setRoot(new TreeItem<>());
        copyView.getRoot().getChildren().addAll((Collection<TreeItem<T>>) batchItemList);
        batchItemList.add(itemList.remove(0));
        while (!verticalScrollbar.isVisible() && itemList.size() > 0) {
            T item = itemList.remove(0);
            batchItemList.add(item);
            copyView.layout();
        }
        if (batchItemList.size() > 1 && verticalScrollbar.isVisible()) {
            T item = batchItemList.remove(batchItemList.size() - 1);
            itemList.add(0, item);
            copyView.layout();
        }
        job.printPage(copyView);
    }

    /**
     * Create a new {@link TableView} that copies serveral settings from the
     * given one but uses the width/height based on the settings of the print
     * job.
     *
     * @param tableView See description.
     * @param job       See description.
     * @return See description.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <T> TreeTableView<T> createTableCopy(TreeTableView<T> tableView, PrinterJob job) {
        TreeTableView<T> copyView = new TreeTableView<>();
        PageLayout pageLayout = job.getJobSettings().getPageLayout();
        Paper paper = pageLayout.getPaper();
        double paperHeight = paper.getHeight() - pageLayout.getTopMargin() - pageLayout.getBottomMargin();
        double paperWidth = paper.getWidth() - pageLayout.getLeftMargin() - pageLayout.getRightMargin();
        if (pageLayout.getPageOrientation().equals(PageOrientation.PORTRAIT)) {
            copyView.setPrefHeight(paperHeight);
            copyView.setPrefWidth(paperWidth);
        } else {
            copyView.setPrefHeight(paperWidth);
            copyView.setPrefWidth(paperHeight);
        }
        copyView.setColumnResizePolicy(tableView.getColumnResizePolicy());
        for (TreeTableColumn<T, ?> t : tableView.getColumns()) {
            TreeTableColumn cloneColumn = new TreeTableColumn(t.getText());
            cloneColumn.setMaxWidth(t.getMaxWidth());
            if (t.getCellValueFactory() != null) {
                cloneColumn.setCellValueFactory(t.getCellValueFactory());
            }
            if (t.getCellFactory() != null) {
                cloneColumn.setCellFactory(t.getCellFactory());
            }
            copyView.getColumns().add(cloneColumn);
        }
        new Scene(copyView);
        // copyView.getScene().getStylesheets().add(TreeTablePrinter.class.getResource("TablePrint.css").toString());
        return copyView;
    }

    /**
     * Searches the vertical scrollbar in the {@link TableView}. The scrollbar
     * won't be available on a off screen {@link TableView} (one that was never
     * added to a visible stage) until at least once the snapshot method was
     * called. The snapshot method is somehow expensive thus it can't be called
     * too often. Thus this entry method is needed.
     *
     * @param tableView See description.
     * @return The found {@link ScrollBar} or <code>null</code>, wenn none was
     * found.
     */
    private static <T> ScrollBar getVerticalScrollbar(TreeTableView<T> tableView) {
        tableView.snapshot(new SnapshotParameters(), null);
        return getVerticalScrollbarForParent(tableView);
    }

    /**
     * Searches for {@link ScrollBar} in the given {@link Parent} but stops when
     * the node is {@link TableCell}
     *
     * @param p See description.
     * @return The found {@link ScrollBar} or <code>null</code>, wenn none was
     * found.
     */
    private static ScrollBar getVerticalScrollbarForParent(Parent p) {
        for (Node n : p.getChildrenUnmodifiable()) {
            if (n instanceof ScrollBar) {
                ScrollBar s = (ScrollBar) n;
                if (s.getOrientation() == Orientation.VERTICAL) {
                    return s;
                }
            }
            if (n instanceof Parent && !(p instanceof TableCell)) {
                ScrollBar scrollbar = getVerticalScrollbarForParent((Parent) n);
                if (scrollbar != null) {
                    return scrollbar;
                }
            }
        }
        return null;
    }
}