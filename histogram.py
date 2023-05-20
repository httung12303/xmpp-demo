import random
import sched
import tkinter as tk
from turtle import width
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

# main properties of the GUI
matplotlib.use("TkAgg")
title = "Data Statistic"
padx = pady = 5
figsize = (4, 3)
dpi = 100

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

# The search bar used to search for a client to show its statistic

# This barchart is to show visualize the corelation between the number of clients and the delay or goodput, 
# and it only serves these 2 type of data, specified in the type attribute.
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
        

# This class retrieves and handles data from the database
class Data:
    "Class Data"

    rows=10
    goodbut_average = [0 for _ in range(10)]
    delay_average = [0 for _ in range(10)]
    connection = mysql.connector.connect(
        host="localhost", user="root", password="12345678", database="xmpp_demo", autocommit=True
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
            
# An array save the items to display
hists = []

# Represent all the stistics, using Tkinter library 
class GUI(tk.Tk):
    # Set up the representation of the main frame
    def __init__(self):
        # Set up the main frame
        super().__init__()
        self.title(title)
        frm_main = tk.Frame(master=self)
        frm_main.grid(row=0, column=0, padx=padx, pady=pady)
        frm_main.grid_columnconfigure((0, 1, 2), weight=1)
        frm_main.grid_rowconfigure((0, 1, 2), weight=1)
        frm_seach_box = tk.Frame(master=frm_main)
        frm_seach_box.grid(row=0, column=0)
        #self.searchBox = Seachbar(frm_seach_box)
        self.table = Table(self)

        # the 'self.data' attribute take responsibility for retreive, handle and update data.
        self.data = Data()
        self.data.getdata()

        hists.append(
            Histogram(
                title="Biểu đồ histogram - Nhiệt độ",
                xlabel="Nhiệt độ",
                ylabel="Số lượng",
                data=self.data.getTemp(),
                bins=np.arange(-4, 40, 5),
                master=frm_main,
                row=0,
                col=0,
            )
        )

        hists.append(
            Histogram(
                title="Biểu đồ histogram - Ánh sáng",
                xlabel="Ánh sáng",
                ylabel="Số lượng",
                data=self.data.getBright(),
                bins=np.arange(0, 800, 100),
                master=frm_main,
                row=0,
                col=1,
            )
        )

        hists.append(
            Histogram(
                title="Biểu đồ histogram - Độ ẩm",
                xlabel="Độ ẩm",
                ylabel="Số lượng",
                data=self.data.getHumid(),
                bins=np.arange(0, 80, 10),
                master=frm_main,
                row=0,
                col=2,
            )
        )

        hists.append(
            BarChart(
                title="Số clients và Goodput trung bình",
                xLabel="Số Client",
                yLabel="ms",
                xPoint=np.arange(0,50,5),
                yPoint=self.data.getGoodput_average(),
                master=frm_main,
                row=1,
                col=0,
                # type='goodput',
            )
        )

        hists.append(
            BarChart(
                xPoint=np.arange(0,50,5),
                title="Số clients và Delay trung bình",
                xLabel="Số Client",
                yLabel="ms",
                yPoint=self.data.getDelay_average(),
                master=frm_main,
                row=1,
                col=1,
                # type='delay',
            )
        )
    
    # Update the GUI after an interval
    def update(self):
        self.data.getdata()
        hists[0].update(self.data.getTemp())
        hists[1].update(self.data.getBright())
        hists[2].update(self.data.getHumid())
        hists[3].update(self.data.getGoodput_average())
        hists[4].update(self.data.getDelay_average())
        self.table.update(self.data.rows)
        self.after(2000, self.update)

class GUI2:
    def __init__(self, data = Data()):
        
        #Khởi tạo cửa sổ và dữ liệu
        self.data = data
        self.window = tk.Tk()
        self.window.title("Data GUI")
        
        self.frame = tk.Frame(self.window)
        self.frame.pack()

        #Tạo các khung thông tin
        self.create_performance_frame()
        self.create_clients_frame()
        self.create_search_bar()
        
        #Cập nhật dữ liệu theo thời gian
        self.update_data()

    #Tạo khung chứa thông tin hiệu năng
    def create_performance_frame(self):
        self.performance_frame = tk.LabelFrame(self.frame, text="Performance")
        self.performance_frame.grid(row=0, column=0)

        #Thông tin trong khung hiệu năng
        self.clients_label = tk.Label(self.performance_frame, text="Number of Client")
        self.clients_label.grid(row=0, column=0, padx=10)
        self.num_of_clients = tk.Label(self.performance_frame, text= self.data.getNumClients())
        self.num_of_clients.grid(row=1, column=0, padx=10)

        self.delay_avg = tk.Label(self.performance_frame, text="Delay Avg")
        self.delay_avg.grid(row=0, column=1, padx=10)
        self.delay_avg_value = tk.Label(self.performance_frame, text = self.data.dl_avg())
        self.delay_avg_value.grid(row=1, column=1, padx=10)

        self.delay_max = tk.Label(self.performance_frame, text="Delay Max")
        self.delay_max.grid(row=0, column=2, padx=10)
        self.delay_max_value = tk.Label(self.performance_frame, text = self.data.getDelay_max())
        self.delay_max_value.grid(row=1, column=2, padx=10)

        self.delay_min = tk.Label(self.performance_frame, text="Delay Min")
        self.delay_min.grid(row=0, column=3, padx=10)
        self.delay_min_value = tk.Label(self.performance_frame, text = self.data.getDelay_min())
        self.delay_min_value.grid(row=1, column=3, padx=10)
        
        self.goodput_avg = tk.Label(self.performance_frame, text="Goodput Avg")
        self.goodput_avg.grid(row=0, column=4, padx=10)
        self.goodput_avg_value = tk.Label(self.performance_frame, text = self.data.gb_avg())
        self.goodput_avg_value.grid(row=1, column=4, padx=10)

        self.goodput_max = tk.Label(self.performance_frame, text="Goodput Max")
        self.goodput_max.grid(row=0, column=5, padx=10)
        self.goodput_max_value = tk.Label(self.performance_frame, text = self.data.getGoodput_max())
        self.goodput_max_value.grid(row=1, column=5, padx=10)

        self.goodput_min = tk.Label(self.performance_frame, text="Goodput Min")
        self.goodput_min.grid(row=0, column=6, padx=10)
        self.goodput_min_value = tk.Label(self.performance_frame, text = self.data.getGoodput_min())
        self.goodput_min_value.grid(row=1, column=6, padx=10)

    #Tạo khung chứa thông tin chi tiết của clients    
    def create_clients_frame(self):
        self.clients_frame = tk.LabelFrame(self.frame, text="Clients Info")
        self.clients_frame.grid(row=1, column=0)

        #Thông tin trong khung clients info
        self.jid_label = tk.Label(self.clients_frame, text="JID")
        self.jid_label.grid(row=1, column=0, padx=10)
        self.jid_value = tk.Label(self.clients_frame, text= "None")
        self.jid_value.grid(row=2, column=0, padx=10)

        self.temp_label = tk.Label(self.clients_frame, text="Temperature")
        self.temp_label.grid(row=1, column=1, padx=10)
        self.temp_value = tk.Label(self.clients_frame, text= "None")
        self.temp_value.grid(row=2, column=1, padx=10)

        self.humid_label = tk.Label(self.clients_frame, text="Humidity")
        self.humid_label.grid(row=1, column=2, padx=10)
        self.humid_value = tk.Label(self.clients_frame, text= "None")
        self.humid_value.grid(row=2, column=2, padx=10)

        self.bright_label = tk.Label(self.clients_frame, text="Brightness")
        self.bright_label.grid(row=1, column=3, padx=10)
        self.bright_value = tk.Label(self.clients_frame, text= "None")
        self.bright_value.grid(row=2, column=3, padx=10)

        self.delay_label = tk.Label(self.clients_frame, text="Delay")
        self.delay_label.grid(row=1, column=4, padx=10)
        self.delay_value = tk.Label(self.clients_frame, text = "None")
        self.delay_value.grid(row=2, column=4, padx=10)

        self.goodput_label = tk.Label(self.clients_frame, text="Goodput")
        self.goodput_label.grid(row=1, column=5, padx=10)
        self.goodput_value = tk.Label(self.clients_frame, text = "None")
        self.goodput_value.grid(row=2, column=5, padx=10)

    #Thêm thanh tìm kiếm
    def create_search_bar(self):
        self.search_label = tk.Label(self.clients_frame, text="Enter client JID:")
        self.search_label.grid(row=0, column=0, padx=10)

        self.search_entry = tk.Entry(self.clients_frame)
        self.search_entry.grid(row=0, column=1, padx=10)

        self.search_button = tk.Button(self.clients_frame, text="Search", command=self.search_client)
        self.search_button.grid(row=0, column=2, padx=10)
        
    #Hàm tìm kiếm clients theo jid:
    def search_client(self):
        search_jid = self.search_entry.get()
        jid = self.data.getJid()
        
        if search_jid in jid:
            index = jid.index(search_jid)
            self.jid_value.config(text=jid[index])
            self.temp_value.config(text=self.data.getTemp()[index])
            self.humid_value.config(text=self.data.getHumid()[index])
            self.bright_value.config(text=self.data.getBright()[index])
            self.delay_value.config(text=self.data.getDelay()[index])
            self.goodput_value.config(text=self.data.getGoodput()[index])
        else:
            self.jid_value.config(text="None")
            self.temp_value.config(text="None")
            self.humid_value.config(text="None")
            self.bright_value.config(text="None")
            self.delay_value.config(text="None")
            self.goodput_value.config(text="None")
            messagebox.showinfo("Not Found", "Client not found.")

    #Hàm update data theo thời gian
    def update_data(self):
        # Cập nhật dữ liệu từ đối tượng Data
        self.data.update()

        # Cập nhật thông tin hiệu năng trong GUI
        self.num_of_clients.config(text=self.data.getNumClients())
        self.delay_avg_value.config(text=self.data.dl_avg())
        self.delay_max_value.config(text=self.data.getDelay_max())
        self.delay_min_value.config(text=self.data.getDelay_min())
        self.goodput_avg_value.config(text=self.data.gb_avg())
        self.goodput_max_value.config(text=self.data.getGoodput_max())
        self.goodput_min_value.config(text=self.data.getGoodput_min())

        # Gọi hàm update_data mỗi 5 giây (có thể điều chỉnh thời gian tùy ý)
        self.window.after(2000, self.update_data)
    
    def run(self):
        self.window.mainloop()


# Sử dụng class Data và class GUI
def code1():
    data = Data()
    data.getdata()
    gui = GUI2(data)
    gui.run()

def code2():
    app = GUI()
    app.configure(background="white")
    app.resizable(False, False)
    app.after(0, app.update)
    app.mainloop()
    app.data.closeConnection()
 
thread1 = threading.Thread(target=code1)
thread2 = threading.Thread(target=code2)

thread1.start()
thread2.start()

thread1.join()
thread2.join()
