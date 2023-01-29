import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

public class GUI implements ActionListener, ItemListener {

    private final double version = 3.03;

    private JTextField targetNameText, targetMsg;
    private JTextArea chatRepliesTextarea;
    private JSpinner spamSpeed;
    private JCheckBox targetSpamCheckBox, targetOnlineCheckbox, seqMsgsCheckbox;
    public JLabel errorMsg;
    public JButton startBtn;
    private JScrollPane scrollPane;

    public GUI() {
        JFrame frame = new JFrame();
        JPanel mainPanel = new JPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        mainPanel.setLayout(new GridBagLayout());

        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 1, 10));

        JLabel targetNameTextLabel = new JLabel("Target name");
        JLabel targetMsgLabel = new JLabel("Target message");
        JLabel spamSpeedLabel = new JLabel("Spam speed (milliseconds)");

        targetNameText = new JTextField();
        targetNameText.setPreferredSize(new Dimension(150, 25));
        targetNameText.setMinimumSize(new Dimension(150, 25));
        targetMsg = new JTextField();
        targetMsg.setPreferredSize(new Dimension(150, 25));
        targetMsg.setMinimumSize(new Dimension(150, 25));
        SpinnerModel sm = new SpinnerNumberModel(1, 1, 99999999, 1);
        spamSpeed = new JSpinner(sm);
        spamSpeed.setPreferredSize(new Dimension(150, 25));
        spamSpeed.setMinimumSize(new Dimension(150, 25));

        targetSpamCheckBox = new JCheckBox("Spamming?");
        targetSpamCheckBox.addActionListener(this);
        targetSpamCheckBox.addItemListener(this);
        seqMsgsCheckbox = new JCheckBox("Sequential?");
        seqMsgsCheckbox.addActionListener(this);
        seqMsgsCheckbox.setEnabled(false);
        targetOnlineCheckbox = new JCheckBox("Online Only?");
        targetOnlineCheckbox.addActionListener(this);

        startBtn = new JButton("Start");
        startBtn.addActionListener(this);

        errorMsg = new JLabel("");

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(targetNameTextLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(targetMsgLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(spamSpeedLabel, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(targetSpamCheckBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(seqMsgsCheckbox, gbc);

        gbc.gridx = 2;
        gbc.gridy = 3;
        mainPanel.add(targetOnlineCheckbox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(targetNameText, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(targetMsg, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(spamSpeed, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 3;
        gbc.weightx = 0.0;
        mainPanel.add(startBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 3;
        gbc.weightx = 0.0;
        errorMsg.setForeground(Color.RED);
        errorMsg.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(errorMsg, gbc);
        errorMsg.setText("Initialising Bot...");


        JPanel chatPanel = new JPanel();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 3, 3, 3);
        chatPanel.setLayout(new GridBagLayout());

        chatRepliesTextarea = new JTextArea(9, 41);
        chatRepliesTextarea.setLineWrap(true);
        chatRepliesTextarea.setWrapStyleWord(true);

        scrollPane = new JScrollPane(chatRepliesTextarea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        constraints.gridx = 0;
        constraints.gridy = 0;
        chatPanel.add(scrollPane, constraints);

        JTabbedPane tp = new JTabbedPane();
        tp.setBounds(50, 50, 200, 200);
        tp.add("Whatsapp Spammer", mainPanel);
        tp.add("Chat Replies", chatPanel);

        frame.add(tp, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(500, 250);
        frame.setLocationRelativeTo(null);
        frame.setTitle("Whatsapp Bot - v" + version);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == startBtn) {
            if (!targetNameText.getText().isEmpty()) {

                if (!targetNameText.getText().equalsIgnoreCase(Whatsapp.targetName) && !Whatsapp.targetName.isEmpty()) {
                    Whatsapp.isSwapTarget = true;
                }

                Whatsapp.targetName = targetNameText.getText();
                Whatsapp.targetMsg.clear();
                Whatsapp.targetMsg.addAll(Arrays.asList(targetMsg.getText().split("=")));

                if (!chatRepliesTextarea.getText().isEmpty()) {
                    if (!Whatsapp.targetChatMsgList.isEmpty() && !Whatsapp.clientChatMsgList.isEmpty()) {
                        Whatsapp.targetChatMsgList.clear();
                        Whatsapp.clientChatMsgList.clear();
                    }
                    for (String chat : chatRepliesTextarea.getText().split("\\n")) {
                        Whatsapp.targetChatMsgList.add(chat.split("=")[0]);
                        Whatsapp.clientChatMsgList.add(chat.split("=")[1]);
                    }
//                    System.out.println("Target chat message list: " + Whatsapp.targetChatMsgList);
//                    System.out.println("Client chat message list: " + Whatsapp.clientChatMsgList);
                }

            } else {
                errorMsg.setText("Error: Target name can't be empty!");
                return;
            }

            Whatsapp.isMsgSpam = targetSpamCheckBox.isSelected();
            Whatsapp.isSeqMsgs = seqMsgsCheckbox.isSelected();
            Whatsapp.spamSpeed = (int) spamSpeed.getValue();

            if (targetOnlineCheckbox.isSelected()) {
                Whatsapp.isSendMsg = true;
                Whatsapp.isSendMsgOnce = true;
            } else {
                Whatsapp.isSendMsg = false;
                Whatsapp.isSendMsgOnce = false;
            }

            if (targetSpamCheckBox.isSelected() && targetOnlineCheckbox.isSelected()) {
                Whatsapp.isSendMsg = true;
                Whatsapp.isSendMsgOnce = true;
            }

            if (startBtn.getText().equalsIgnoreCase("Resume Bot")) {
                startBtn.setText("Pause Bot");
                targetNameText.setEnabled(false);
                targetMsg.setEnabled(false);
                spamSpeed.setEnabled(false);
                targetSpamCheckBox.setEnabled(false);
                seqMsgsCheckbox.setEnabled(false);
                targetOnlineCheckbox.setEnabled(false);
                scrollPane.setEnabled(false);
                chatRepliesTextarea.setEnabled(false);
                return;
            }

            if (startBtn.getText().equalsIgnoreCase("Pause Bot")) {
                startBtn.setText("Resume Bot");
                targetNameText.setEnabled(true);
                targetMsg.setEnabled(true);
                spamSpeed.setEnabled(true);
                targetSpamCheckBox.setEnabled(true);
                seqMsgsCheckbox.setEnabled(true);
                targetOnlineCheckbox.setEnabled(true);
                scrollPane.setEnabled(true);
                chatRepliesTextarea.setEnabled(true);
                errorMsg.setForeground(Color.RED);
                errorMsg.setText("Paused...");
                return;
            }

            Whatsapp.isStartBot = true;
            startBtn.setText("Pause Bot");

            targetNameText.setEnabled(false);
            targetMsg.setEnabled(false);
            spamSpeed.setEnabled(false);
            targetSpamCheckBox.setEnabled(false);
            seqMsgsCheckbox.setEnabled(false);
            targetOnlineCheckbox.setEnabled(false);
            scrollPane.setEnabled(false);
            chatRepliesTextarea.setEnabled(false);
            errorMsg.setText("");
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == targetSpamCheckBox) {
            if (targetSpamCheckBox.isSelected()) {
                seqMsgsCheckbox.setEnabled(true);
            } else {
                seqMsgsCheckbox.setSelected(false);
                seqMsgsCheckbox.setEnabled(false);
            }
        }
    }
}
