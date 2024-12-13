package view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author karsten
 */
public class Menu<I, O> {

    private String selectActionMessage = "Select an action: ";
    private boolean confirmation = false;
    private Function<I, String> display = null;
    private Function<O, String> selectDisplay = o -> o.toString();
    private boolean exit = true;

    private String title;
    protected Menu<?, I> parent;
    protected O state = null;
    private List<Menu> submenus = new ArrayList();

    public Menu() {
    }

    public Menu(String title) {
        this.title = title;
    }

    public Menu add(Menu submenu) {
        submenu.parent = this;
        submenus.add(submenu);

        return this;
    }

    public Menu stateDisplay(Function<I, String> display) {
        this.display = display;

        return this;
    }

    public Menu selectionDisplay(Function<O, String> display) {
        if (display == null) {
            throw new IllegalArgumentException("Display type cannot be null");
        }

        this.selectDisplay = display;

        return this;
    }

    public Menu confirmation() {
        confirmation = true;

        return this;
    }

    public Menu noExit() {
        exit = false;

        return this;
    }

    private boolean hasSubmenus() {
        return !submenus.isEmpty();
    }

    private Menu selectSubmenu() {
        int counter = 1;

        for (Menu submenu : submenus) {
            System.out.println(counter + ": " + submenu.getTitle());
            counter++;
        }

        if (exit) {
            if (parent == null) {
                System.out.println(counter + ": Exit");
            } else {
                System.out.println(counter + ": Back");
            }
        }

        int result = Utilities.getInt(selectActionMessage, 1, submenus.size() + ((exit) ? 1 : 0));

        // If there is an exit, the resulting value will be null
        if (result > submenus.size()) {
            return null;
        }

        return submenus.get(result - 1);
    }

    private void displayParent() {
        if (parent.state == null) {
            return;
        }

        System.out.println(display.apply(parent.state));
    }

    protected void performAction() {
        // This will be overrided
    }

    public void run() {
        if (title != null) {
            System.out.printf("\n--- %s ---\n", title);
        }

        // If confirmation is need and none is give, return
        if (confirmation && !Utilities.getConfirmation()) {
            // If exit or no submenu, run the parent again
            if (parent != null) {
                parent.run();
            }

            return;
        }

        if (display != null) {
            displayParent();
        }

        if (state == null) {
            performAction();
        }

        // If there are submenus, select from one
        if (hasSubmenus()) {
            Menu submenu = selectSubmenu();

            // Exit wasn't selected
            if (submenu != null) {
                submenu.run();
                return;
            }
        }

        state = null;

        // If exit or no submenu, run the parent again
        if (parent != null) {
            parent.run();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSelectActionMessage() {
        return selectActionMessage;
    }

    public void setSelectActionMessage(String selectActionMessage) {
        this.selectActionMessage = selectActionMessage;
    }

    protected void selectItem(List<O> items, String message) {
        int counter = 1;

        for (O item : items) {
            System.out.println(counter + ": " + selectDisplay.apply(item));
            counter++;
        }

        int index = Utilities.getInt(message, 0, items.size());

        if (index == 0) {
            state = null;
        }

        state = items.get(index - 1);
    }

    public O getState() {
        return state;
    }

    public Menu<?, I> getParent() {
        return parent;
    }

}
