import random
import sched
import tkinter as tk
import matplotlib
import numpy as np
from tkinter import ttk
import matplotlib.pyplot as plt
from mysql.connector import connect
import mysql.connector
from tkintertable import TableCanvas, TableModel
from datetime import datetime
from tkinter import *
from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib.animation import FuncAnimation
import time
from tkinter import messagebox
import sys
import threading

# This class retrieves and handles data from the database
class Data:
    "Class Data"

    rows=10
    goodbut_average = [0 for _ in range(10)]
    delay_average = [0 for _ in range(10)]
    connection = mysql.connector.connect(
        host="localhost", user="root", password="datto1234", database="xmpp_demo", autocommit=True
        )
    cursor = connection.cursor()

    def closeConnection(self):
        self.connection.close()
        self.cursor.close()

    def getdata(self):
        query = (
            "SELECT jid, temperature, brightness, humidity, goodput, delay FROM clients;"
        )
        self.cursor.execute(query)
        self.rows = self.cursor.fetchall()

    def getNumClients(self):
        return len(self.rows)

    def getJid(self):
        count = len(self.rows)
        jid = [0 for _ in range(count)]
        i=0
        for res in self.rows:
            jid[i] = res[0]
            i+=1
        return jid

    def getTemp(self):
        count = len(self.rows)
        temp = [0 for _ in range(count)]
        i=0
        for res in self.rows:
            temp[i] = res[1]
            i+=1
        return temp

    def getBright(self):
        count = len(self.rows)
        bright = [0 for _ in range(count)]
        i=0
        for res in self.rows:
            bright[i] = res[2]
            i+=1
        return bright

    def getHumid(self):
        count = len(self.rows)
        humid = [0 for _ in range(count)]
        i=0
        for res in self.rows:
            humid[i] = res[3]
            i+=1
        return humid

    def getGoodput(self):
        count = len(self.rows)
        goodput = [0 for _ in range(count)]
        i=0
        for res in self.rows:
            goodput[i] = res[4]
            i+=1
        return goodput

    def getDelay(self):
        count = len(self.rows)
        delay = [0 for _ in range(count)]
        i=0
        for res in self.rows:
            delay[i] = res[5]
            i+=1
        return delay

    def getGoodput_min(self):
        min_goodput = sys.maxsize
        for res in self.rows:
            min_goodput = min(min_goodput, res[4])
        return min_goodput

    def getGoodput_max(self):
        max_goodput = 0
        for res in self.rows:
            max_goodput = max(max_goodput, res[4])
        return max_goodput

    def getGoodput_average(self):
        count = len(self.rows)
        total_goodput = 0
        for res in self.rows:
            total_goodput += res[4]
        index = 9 - int((50-count)/5)
        if count>0:
            self.goodbut_average[index] = total_goodput/count
        return self.goodbut_average

    def gb_avg(self):
        count = len(self.rows)
        total_goodput = 0
        for res in self.rows:
            total_goodput += res[4]
        if count>0:
            return total_goodput/count
        else:
            return 0

    def getDelay_min(self):
        min_delay = sys.maxsize
        for res in self.rows:
            min_delay = min(min_delay, res[5])
        return min_delay

    def getDelay_max(self):
        max_delay = 0
        for res in self.rows:
            max_delay = max(max_delay, res[5])
        return max_delay

    def getDelay_average(self):
        count = len(self.rows)
        total_delay = 0
        for res in self.rows:
            total_delay += res[5]
        index = 9 - int((50-count)/5)
        if count>0:
            self.delay_average[index] = total_delay/count
        return self.delay_average

    def dl_avg(self):
        count = len(self.rows)
        total_delay = 0
        for res in self.rows:
            total_delay += res[5]
        if count>0:
            return total_delay/count
        else:
            return 0


    def update(self):
        self.getdata()

# Display a new window with a list of clients which existed in the database
class Table:
    def __init__(self, master):
        newWindow = Toplevel(master, width=200)
        self.table = ttk.Treeview(newWindow)
        self.table.pack()
        self.table["columns"] = (
            "jid",
            "time",
            "temperature",
            "humidity",
            "brightness",
            "delay",
            "goodput",
            "last_update",
        )
        self.table.heading("#0", text="ID")
        self.table.heading("jid", text="JID")
        self.table.heading("time", text="Time")
        self.table.heading("temperature", text="Temperature")
        self.table.heading("humidity", text="Humidity")
        self.table.heading("brightness", text="Brightness")
        self.table.heading("delay", text="Delay")
        self.table.heading("goodput", text="Goodput")
        self.table.heading("last_update", text="Last Update")

    def update(self, data):
        self.table.delete(*self.table.get_children())

        for i, row in enumerate(data):
            last_update_timestamp = row[-1] / 1000
            last_update_datetime = datetime.fromtimestamp(last_update_timestamp)
            row = list(row)
            row[-1] = last_update_datetime.strftime(
                "%Y-%m-%d %H:%M:%S"
            )  # Định dạng datetime
            self.table.insert("", tk.END, text=str(i + 1), values=row)

# This barchart is to show visualize the corelation between the number of clients and the delay or goodput.
class BarChart:
    def __init__(self, title, xLabel, yLabel, xPoint, yPoint, master, row, col):
        figure = Figure(figsize=figsize, dpi=dpi)
        self.figure_canvas = FigureCanvasTkAgg(figure, master=master)

        # Tilte, label for x and y axis
        self.xLabel = xLabel
        self.yLabel = yLabel
        self.title = title

        self.axes = figure.add_subplot()
        self.xPoint = xPoint
        self.yPoint = yPoint
        self.xSize = len(yPoint)
        self.axes.bar(xPoint, yPoint)
        self.axes.set_title(title)
        self.axes.set_xlabel(xlabel=xLabel)
        self.axes.set_ylabel(ylabel=yLabel)
        #self.axes.legend()
        self.figure_canvas.get_tk_widget().grid(row=row, column=col)
    def update(self, yPoints):
        self.axes.cla()
        self.axes.set_title(self.title)
        self.axes.set_xlabel(self.xLabel)
        self.axes.set_ylabel(self.yLabel)
        self.yPoints = np.zeros(10)
        self.axes.bar(self.xPoint, yPoints)
        self.figure_canvas.draw_idle()

# This histogram show the distribution of the temerature or humidity or brightness which base on the number of clients
class Histogram:

    def __init__(self, title, xlabel, ylabel, data, bins, master, row, col):
        # create a figure
        self.figure = Figure(figsize=figsize, dpi=dpi)

        # create FigureCanvasTkAgg object
        self.figure_canvas = FigureCanvasTkAgg(self.figure, master=master)

        # create axes
        self.axes = self.figure.add_subplot()

        # Tilte, label for x and y axis
        self.xLabel = xlabel
        self.yLabel = ylabel
        self.title = title

        # create the barchart
        self.bins = bins
        self.axes.hist(data, bins)
        self.axes.set_title(title)
        self.axes.set_xlabel(xlabel)
        self.axes.set_ylabel(ylabel)
        self.figure_canvas.get_tk_widget().grid(row=row, column=col)

    def update(self, data):
        self.axes.cla()
        self.axes.hist(data, self.bins)
        self.axes.set_title(self.title)
        self.axes.set_xlabel(self.xLabel)
        self.axes.set_ylabel(self.yLabel)
        self.figure_canvas.draw_idle()