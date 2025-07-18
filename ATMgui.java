import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.*;

public class ATMgui {
    private float balance = 0;
    private int pin = 1019;
    private int pinAttempts = 0;
    private HashMap<String, Float> accounts = new HashMap<>();
    private ArrayList<String> miniStatement = new ArrayList<>();
    private JFrame frame;

    // Decimal format for displaying money properly
    private DecimalFormat df = new DecimalFormat("#,##0.00");

    public ATMgui() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        accounts.put("12345", 10000f);
        accounts.put("67890", 20000f);
        showLoginScreen();
    }

    public void showLoginScreen() {
        frame = new JFrame("ATM - Login");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Enter PIN:");
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        JPasswordField pinField = new JPasswordField(10);
        pinField.setFont(new Font("Arial", Font.PLAIN, 20));
        JButton loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 18));

        loginBtn.addActionListener(e -> {
            String entered = new String(pinField.getPassword());
            if (entered.equals(String.valueOf(pin))) {
                pinAttempts = 0;
                frame.dispose();
                showMenu();
            } else {
                pinAttempts++;
                if (pinAttempts >= 3) {
                    JOptionPane.showMessageDialog(frame, "Too many attempts. Exiting.");
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(frame, "Incorrect PIN. Attempts left: " + (3 - pinAttempts));
                }
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(label, gbc);
        gbc.gridx = 1;
        frame.add(pinField, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        frame.add(loginBtn, gbc);

        frame.setVisible(true);
    }

    public void showMenu() {
        frame = new JFrame("ATM - Menu");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(7, 1, 20, 20));
        frame.setLocationRelativeTo(null);

        Font btnFont = new Font("Arial", Font.BOLD, 18);

        JButton balanceBtn = new JButton("Check Balance");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer Funds");
        JButton miniStmtBtn = new JButton("Mini Statement");
        JButton changePinBtn = new JButton("Change PIN");
        JButton exitBtn = new JButton("Exit");

        for (JButton btn : new JButton[]{balanceBtn, depositBtn, withdrawBtn, transferBtn, miniStmtBtn, changePinBtn, exitBtn}) {
            btn.setFont(btnFont);
            frame.add(btn);
        }

        balanceBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Balance: ₹" + df.format(balance)));
        depositBtn.addActionListener(e -> deposit());
        withdrawBtn.addActionListener(e -> withdraw());
        transferBtn.addActionListener(e -> transferFunds());
        miniStmtBtn.addActionListener(e -> showMiniStatement());
        changePinBtn.addActionListener(e -> changePin());
        exitBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Thank you for using the ATM!");
            System.exit(0);
        });

        frame.setVisible(true);
    }

    public void deposit() {
        String input = JOptionPane.showInputDialog(frame, "Enter amount to deposit:");
        try {
            float amount = Float.parseFloat(input);
            balance += amount;
            miniStatement.add("Deposited: ₹" + df.format(amount));
            JOptionPane.showMessageDialog(frame, "Deposit Successful!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid amount!");
        }
    }

    public void withdraw() {
        String input = JOptionPane.showInputDialog(frame, "Enter amount to withdraw:");
        try {
            float amount = Float.parseFloat(input);
            if (amount > balance) {
                JOptionPane.showMessageDialog(frame, "Insufficient Balance!");
            } else {
                balance -= amount;
                miniStatement.add("Withdrawn: ₹" + df.format(amount));
                JOptionPane.showMessageDialog(frame, "Withdrawal Successful!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid amount!");
        }
    }

    public void transferFunds() {
        String acc = JOptionPane.showInputDialog(frame, "Enter recipient account number:");
        if (!accounts.containsKey(acc)) {
            JOptionPane.showMessageDialog(frame, "Invalid account number!");
            return;
        }

        String input = JOptionPane.showInputDialog(frame, "Enter amount to transfer:");
        try {
            float amt = Float.parseFloat(input);
            if (amt > balance) {
                JOptionPane.showMessageDialog(frame, "Insufficient Balance!");
                return;
            }
            balance -= amt;
            accounts.put(acc, accounts.get(acc) + amt);
            miniStatement.add("Transferred ₹" + df.format(amt) + " to Acc: " + acc);
            JOptionPane.showMessageDialog(frame, "Transfer Successful!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Invalid amount!");
        }
    }

    public void showMiniStatement() {
        if (miniStatement.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No transactions yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String t : miniStatement) {
            sb.append(t).append("\n");
        }
        sb.append("\nCurrent Balance: ₹").append(df.format(balance));
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JOptionPane.showMessageDialog(frame, new JScrollPane(textArea), "Mini Statement", JOptionPane.INFORMATION_MESSAGE);
    }

    public void changePin() {
        String oldPin = JOptionPane.showInputDialog(frame, "Enter current PIN:");
        if (!oldPin.equals(String.valueOf(pin))) {
            JOptionPane.showMessageDialog(frame, "Incorrect current PIN!");
            return;
        }

        String newPin = JOptionPane.showInputDialog(frame, "Enter new PIN:");
        String reNew = JOptionPane.showInputDialog(frame, "Re-enter new PIN:");
        if (newPin.equals(reNew)) {
            pin = Integer.parseInt(newPin);
            JOptionPane.showMessageDialog(frame, "PIN changed successfully!");
        } else {
            JOptionPane.showMessageDialog(frame, "PINs do not match!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ATMgui::new);
    }
}
