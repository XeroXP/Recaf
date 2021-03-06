package me.coley.recaf.ui.component.internalframe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import me.coley.recaf.Recaf;
import me.coley.recaf.ui.Lang;
import me.coley.recaf.ui.component.LabeledComponent;
import me.coley.recaf.ui.component.action.ActionButton;
import me.coley.recaf.ui.component.action.ActionTextField;
import me.coley.recaf.ui.component.panel.LabelSwitcherPanel;

@SuppressWarnings("serial")
public class TryCatchBox extends BasicFrame {
	private static final Color bg = new Color(166, 166, 166);
	private final JScrollPane scroll = new JScrollPane();

	public TryCatchBox(MethodNode mn) {
		super(Lang.get("window.catch.prefix") + mn.name);
		setBackground(bg);
		setLayout(new BorderLayout());
		add(scroll, BorderLayout.CENTER);

		update(mn);
		int k = 162;
		int s = Math.min(k * 2, k * (mn.tryCatchBlocks.size() + 1));
		scroll.setPreferredSize(new Dimension(350, s));
		setVisible(true);
	}

	private void update(MethodNode mn) {
		JPanel content = new JPanel();
		content.setLayout(new GridLayout(0, 1));
		for (int i = 0; i <= mn.tryCatchBlocks.size(); i++) {
			content.add(make(i, mn));
		}
		scroll.setViewportView(content);
	}

	private JPanel make(final int i, MethodNode mn) {
		boolean isNew = i >= mn.tryCatchBlocks.size();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEtchedBorder());
		TryCatchBlockNode block;
		if (isNew) {
			block = new TryCatchBlockNode(null, null, null, "");
		} else {
			block = mn.tryCatchBlocks.get(i);
		}
		List<JComponent> comps = new ArrayList<>();
		comps.add(new LabeledComponent(Lang.get("window.method.catch.start"), new LabelSwitcherPanel(mn, block.start, l -> block.start = l)));
		comps.add(new LabeledComponent(Lang.get("window.method.catch.end"), new LabelSwitcherPanel(mn, block.end, l -> block.end = l)));
		comps.add(new LabeledComponent(Lang.get("window.method.catch.handler"), new LabelSwitcherPanel(mn, block.handler,
				l -> block.handler = l)));
		comps.add(new LabeledComponent(Lang.get("window.method.catch.type"), new ActionTextField(block.type, s -> block.type = s)));
		if (isNew) {
			comps.add(new ActionButton(Lang.get("misc.add"), () -> {
				if (block.start == null || block.end == null || block.handler == null) {
					return;
				}  else {
					Recaf.INSTANCE.ui.setTempTile(Lang.get("misc.warn.fill.all"), 1000);
				}
				mn.tryCatchBlocks.add(block);
				update(mn);
			}));
		} else {
			comps.add(new ActionButton(Lang.get("misc.delete"), () -> {
				if (Recaf.INSTANCE.configs.ui.confirmDeletions) {
					int dialogResult = JOptionPane.showConfirmDialog(null, Lang.get("misc.warn.catch"), Lang.get("misc.warn.title"),
							JOptionPane.YES_NO_OPTION);
					if (dialogResult != JOptionPane.YES_OPTION) {
						return;
					}
				}
				mn.tryCatchBlocks.remove(i);
				update(mn);
			}));
		}
		for (JComponent c : comps) {
			c.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			panel.add(c);
		}
		return panel;
	}
}