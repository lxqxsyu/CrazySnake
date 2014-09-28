package com.example.crazysnake;

import java.io.Serializable;

public class Box implements Serializable {
	private static final long serialVersionUID = -4292822712732467393L;
	private int x;
	private int y;

	public Box(int x, int y) {
		super();
		this.x = x; // x方向的位置 （从0开始）
		this.y = y; // y方向的位置
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
