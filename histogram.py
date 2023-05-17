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

matplotlib.use("TkAgg")

from matplotlib.figure import Figure
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg
from matplotlib.animation import FuncAnimation

title = "title example"
padx = pady = 5
figsize = (6, 5)
dpi = 100

x_vals = []
temperature = []
humidity = []
brightness = []
delay = []
goodput = []
jids = []

goodput_tb =[0 for _ in range(10)]
delay_tb=[0 for _ in range(10)]
goodput_max = 0
goodput_min = 0
delay_max = 0
delay_min = 0
# jid_vals = []


# db = connect(host="localhost", user="root", password="123456789", database="xmpp_demo")
# cur = db.cursor()

# cur.execute("SELECT * FROM clients")
# results = cur.fetchall()


# for jid, time, temp, humid, bri, delay_val, goodput_val, lastupdate in results:
#     x_vals.append(str(time))
#     temperature.append(float(temp))
#     humidity.append(int(humid))
#     brightness.append(int(bri))
#     delay.append(float(delay_val))
#     goodput.append(float(goodput_val))
#     jid_vals.append(jid)

# client_count = len(jid_vals)


# if all(value == 0 for value in delay):
#     print("Không có dữ liệu delay để tính trung bình.")
# else:
average_delay = 0

# if all(value == 0 for value in goodput):
#     print("Không có dữ liệu goodput để tính trung bình.")
# else:
average_goodput = 0
# average_goodput = sum(goodput) / len(goodput)


class Seachbar:
    def __init__(self, master):
        ttk.Label(
            master, text="Chọn jid của client", font=("Times New Roman", 11)
        ).pack()
        n = tk.StringVar()
        self.clientChoosen = ttk.Combobox(
            master, width=(4 + 1 + 4 * 3 + 3), textvariable=n
        )
        # self.clientChoosen["value"] = jid_vals
        self.clientChoosen.pack()
        self.clientChoosen.current()

    def update(self, data):
        self.clientChoosen["value"] = data


class Table:
    def __init__(self, master):
        newWindow = Toplevel(master)
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


class Histogram:
    def __init__(self, title, xlabel, ylabel, data, bins, master, row, col):
        # create a figure
        self.figure = Figure(figsize=figsize, dpi=dpi)

        # create FigureCanvasTkAgg object
        self.figure_canvas = FigureCanvasTkAgg(self.figure, master=master)

        # create axes
        self.axes = self.figure.add_subplot()

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
        # self.figure.canvas.draw_idle()
        self.figure_canvas.draw_idle()


class Correlate:
    def __init__(self, title, label, xlabel, ylabel, data, _range, master, row, col):
        # create a figure
        figure = Figure(figsize=figsize, dpi=dpi)

        # create FigureCanvasTkAgg object
        self.figure_canvas = FigureCanvasTkAgg(figure, master=master)

        # create axes
        self.axes = figure.add_subplot()

        self._range = _range
        self.label = label
        # create the barchart
        self.axes.plot(_range, [data] * 100, label=label)
        self.axes.set_title(title)
        self.axes.set_xlabel(xlabel)
        self.axes.set_ylabel(ylabel)
        self.axes.legend()
        self.figure_canvas.get_tk_widget().grid(row=row, column=col)

    def update(self, data):
        self.axes.cla()
        # average_delay = sum(delay) / len(delay)
        self.axes.plot(self._range, [data] * 100, label=self.label)
        # self.figure.canvas.draw_idle()
        self.figure_canvas.draw_idle()


hists = []


class App(tk.Tk):
    def __init__(self):
        super().__init__()

        self.title(title)

        frm_main = tk.Frame(master=self)
        frm_main.grid(row=0, column=0, padx=padx, pady=pady)
        # frm_main.grid_columnconfigure(3, minsize=50)
        # frm_main.grid_rowconfigure(2, minsize=50)
        frm_main.grid_columnconfigure((0, 1, 2), weight=1)
        frm_main.grid_rowconfigure((0, 1, 2), weight=1)

        frm_seach_box = tk.Frame(master=frm_main)
        frm_seach_box.grid(row=0, column=0)
        self.searchBox = Seachbar(frm_seach_box)
        self.table = Table(self)

        hists.append(
            Histogram(
                title="Biểu đồ histogram - Nhiệt độ",
                xlabel="Nhiệt độ",
                ylabel="Số lượng",
                data=temperature,
                bins=np.arange(-4, 40, 5),
                master=frm_main,
                row=0,
                col=1,
            )
        )

        hists.append(
            Histogram(
                title="Biểu đồ histogram - Ánh sáng",
                xlabel="Ánh sáng",
                ylabel="Số lượng",
                data=brightness,
                bins=np.arange(0, 800, 100),
                master=frm_main,
                row=0,
                col=2,
            )
        )

        hists.append(
            Histogram(
                title="Biểu đồ histogram - Độ ẩm",
                xlabel="Độ ẩm",
                ylabel="Số lượng",
                data=brightness,
                bins=np.arange(0, 80, 10),
                master=frm_main,
                row=1,
                col=0,
            )
        )

        hists.append(
            Correlate(
                title="Sự tương quan giữa Goodput Trung bình và số Clients",
                label="Goodput Trung bình",
                xlabel="Số lượng Clients",
                ylabel="Byte per second",
                data=average_goodput,
                _range=range(0, 100),
                master=frm_main,
                row=1,
                col=1,
            )
        )

        hists.append(
            Correlate(
                title="Sự tương quan giữa Delay Trung bình và số Clients",
                label="Delay Trung bình",
                xlabel="Số lượng Clients",
                ylabel="Ms",
                data=average_goodput,
                _range=range(0, 100),
                master=frm_main,
                row=1,
                col=2,
            )
        )


if __name__ == "__main__":
    app = App()
    data = [jids, temperature, brightness, humidity, goodput, delay]
    connection = mysql.connector.connect(
        host="localhost", user="root", password="123456789", database="xmpp_demo", autocommit=True
    )

    def laydulieu_goodput(arr):

        for i in range(len(data)):
            data[i].clear()

        cursor = connection.cursor()

        query = (
            "SELECT goodput FROM clients;"
        )
        cursor.execute(query)
        results = cursor.fetchall()
        total_goodput = 0
        count = 0
        global goodput_min
        global goodput_max
        res1 = results[0]
        goodput_min=res1[0]
        #goodput_min = results[0]
        for res in results:
            if goodput_max < (res[0]+0):
                goodput_max = (res[0]+0)
            if goodput_min > (res[0]+0):
                goodput_min = (res[0]+0)
            total_goodput += res[0]
            count+= 1

        index = (50-count)/5
        index = int(index)
        index= 9-index
        arr[index] = total_goodput/count


    def laydulieu_delay(arr):

        for i in range(len(data)):
            data[i].clear()

        cursor = connection.cursor()

        query = (
            "SELECT delay FROM clients;"
        )
        cursor.execute(query)
        results = cursor.fetchall()
        total_delay = 0
        count = 0
        global delay_min
        global delay_max
        res1 = results[0]
        delay_min=res1[0]
        for res in results:
            if delay_max < (res[0]+0):
                delay_max = (res[0]+0)
            if delay_min > (res[0]+0):
                delay_min = (res[0]+0)
            total_delay += res[0]
            count+= 1
        index = (50-count)/5
        index = int(index)
        index= 9-index
        arr[index] = total_delay/count

    def update():

        for i in range(len(data)):
            data[i].clear()

        cursor = connection.cursor()

        query = (
            "SELECT jid, temperature, brightness, humidity, goodput, delay FROM clients;"
        )
        cursor.execute(query)
        results = cursor.fetchall()
        # print(results)
        for res in results:
            for i in range(len(res)):
                data[i].append(res[i])

        for i in range(len(data[slice(len(res) - 1)])):
            hists[i].update(data[i + 1])
        app.searchBox.update(data[0])

        # if app.searchBox.clientChoosen.get() != "":
        query = "SELECT * FROM clients"
        cursor.execute(query)
        res = cursor.fetchall()
        app.table.update(res)

        cursor.close()

        app.after(0, laydulieu_goodput(goodput_tb))
        app.after(0, laydulieu_delay(delay_tb))
        print("Trung binh goodput: ", goodput_tb)
        print("Trung binh delay: ", delay_tb)
        print("goodput max: ", goodput_max)
        print("goodput min: ", goodput_min)
        print("Delay max: ", delay_max)
        print("Delay min: ", delay_min)
        print(" ")

        app.after(5000, update)


    app.configure(background="white")
    app.resizable(False, False)
    app.after(0, update)
    app.mainloop()
    connection.close()
