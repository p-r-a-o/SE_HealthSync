"use client"

import { useState, useEffect } from "react"
import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import api from "@/lib/api"
import BillModal from "@/components/bill-modal"
import { X, Edit2, Plus, Trash2, CheckCircle, Clock, AlertCircle } from "lucide-react"

export default function ReceptionistBillsPage() {
  const [bills, setBills] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState("")
  const [filter, setFilter] = useState("ALL")
  const [selectedBill, setSelectedBill] = useState<any>(null)
  const [showModal, setShowModal] = useState(false)
  const [editingBill, setEditingBill] = useState<any>(null)
  const [paymentAmount, setPaymentAmount] = useState("")
  const [searchQuery, setSearchQuery] = useState("")

  useEffect(() => {
    fetchBills()
  }, [filter])

  const fetchBills = async () => {
    try {
      setLoading(true)
      setError("")
      let response
      if (filter === "UNPAID") {
        response = await api.get("/bills/unpaid")
      } else if (filter === "PAID") {
        response = await api.get("/bills/status/PAID")
      } else {
        response = await api.get("/bills")
      }
      setBills(response.data || [])
    } catch (err: any) {
      setError("Failed to load bills")
    } finally {
      setLoading(false)
    }
  }

  const handleOpenNewBill = () => {
    setEditingBill(null)
    setShowModal(true)
  }

  const handleEditBill = (bill: any) => {
    setEditingBill(bill)
    setShowModal(true)
  }

  const handleDeleteBill = async (billId: string) => {
    if (window.confirm("Are you sure you want to delete this bill?")) {
      try {
        await api.delete(`/bills/${billId}`)
        setSuccess("Bill deleted successfully")
        fetchBills()
        setTimeout(() => setSuccess(""), 3000)
      } catch (err: any) {
        setError("Failed to delete bill")
        setTimeout(() => setError(""), 3000)
      }
    }
  }

  const handleProcessPayment = async () => {
    if (!selectedBill || !paymentAmount) {
      setError("Please enter payment amount")
      setTimeout(() => setError(""), 3000)
      return
    }

    const amount = Number.parseFloat(paymentAmount)
    const balance = selectedBill.balanceAmount

    if (amount <= 0 || amount > balance) {
      setError(`Payment amount must be between 0 and ${balance}`)
      setTimeout(() => setError(""), 3000)
      return
    }

    try {
      await api.post(`/bills/${selectedBill.billId}/payment?amount=${amount}`)
      setSuccess("Payment processed successfully")
      setPaymentAmount("")
      fetchBills()
      setTimeout(() => setSuccess(""), 3000)
    } catch (err: any) {
      setError("Failed to process payment")
      setTimeout(() => setError(""), 3000)
    }
  }

  const filteredBills = bills.filter(
    (bill: any) =>
      bill.patientName?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      bill.billId?.toLowerCase().includes(searchQuery.toLowerCase()),
  )

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "PAID":
        return <CheckCircle className="w-5 h-5 text-green-600" />
      case "PENDING":
        return <Clock className="w-5 h-5 text-orange-600" />
      default:
        return <AlertCircle className="w-5 h-5 text-red-600" />
    }
  }

  if (loading)
    return (
      <div className="container mx-auto p-6 flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">Loading bills...</p>
        </div>
      </div>
    )

  return (
    <div className="container mx-auto p-6 bg-gradient-to-br from-gray-50 to-gray-100 min-h-screen">
      {/* Header */}
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-4xl font-bold text-gray-900">Billing Management</h1>
          <p className="text-gray-600 mt-2">Manage patient bills, payments, and invoices</p>
        </div>
        <Button
          onClick={handleOpenNewBill}
          className="bg-blue-600 hover:bg-blue-700 text-white flex items-center gap-2"
        >
          <Plus className="w-5 h-5" />
          Create New Bill
        </Button>
      </div>

      {/* Alerts */}
      {error && (
        <div className="mb-4 p-4 bg-red-50 border-l-4 border-red-600 text-red-700 rounded flex items-start justify-between">
          <span>{error}</span>
          <button onClick={() => setError("")}>
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {success && (
        <div className="mb-4 p-4 bg-green-50 border-l-4 border-green-600 text-green-700 rounded flex items-start justify-between">
          <span>{success}</span>
          <button onClick={() => setSuccess("")}>
            <X className="w-4 h-4" />
          </button>
        </div>
      )}

      {/* Filter and Search */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-8">
        <div>
          <input
            type="text"
            placeholder="Search by patient name or bill ID..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
        <div className="flex gap-2 flex-wrap">
          {["ALL", "PAID", "UNPAID"].map((status) => (
            <Button
              key={status}
              onClick={() => setFilter(status)}
              className={`flex-1 ${
                filter === status
                  ? "bg-blue-600 text-white"
                  : "bg-white text-gray-700 border border-gray-300 hover:bg-gray-50"
              }`}
            >
              {status}
            </Button>
          ))}
        </div>
      </div>

      {/* Main Content */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Bills List */}
        <div className="lg:col-span-2">
          {filteredBills.length === 0 ? (
            <Card className="p-12 text-center bg-white">
              <AlertCircle className="w-12 h-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-600 text-lg">No bills found</p>
            </Card>
          ) : (
            <div className="space-y-4">
              {filteredBills.map((bill: any) => (
                <Card
                  key={bill.billId}
                  className={`p-6 cursor-pointer transition-all border-l-4 ${
                    selectedBill?.billId === bill.billId
                      ? "ring-2 ring-blue-500 border-l-blue-600 shadow-lg"
                      : `border-l-${bill.status === "PAID" ? "green" : "orange"}-500 hover:shadow-md`
                  }`}
                  onClick={() => setSelectedBill(bill)}
                >
                  <div className="flex justify-between items-start mb-4">
                    <div>
                      <p className="text-sm text-gray-600">Bill ID</p>
                      <p className="font-mono font-semibold text-gray-900">{bill.billId}</p>
                    </div>
                    <div className="flex gap-2">
                      {getStatusIcon(bill.status)}
                      <span
                        className={`text-sm font-semibold px-3 py-1 rounded ${
                          bill.status === "PAID" ? "bg-green-100 text-green-700" : "bg-orange-100 text-orange-700"
                        }`}
                      >
                        {bill.status}
                      </span>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div>
                      <p className="text-sm text-gray-600">Patient</p>
                      <p className="font-semibold text-gray-900">{bill.patientName}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Total Amount</p>
                      <p className="font-semibold text-lg text-blue-600">₹{bill.totalAmount?.toFixed(2)}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Paid Amount</p>
                      <p className="font-semibold text-gray-900">₹{bill.paidAmount?.toFixed(2)}</p>
                    </div>
                    <div>
                      <p className="text-sm text-gray-600">Balance</p>
                      <p className={`font-semibold ${bill.balanceAmount > 0 ? "text-red-600" : "text-green-600"}`}>
                        ₹{bill.balanceAmount?.toFixed(2)}
                      </p>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          )}
        </div>

        {/* Bill Details Sidebar */}
        {selectedBill ? (
          <Card className="p-6 h-fit sticky top-6 bg-white">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold text-gray-900">Bill Details</h2>
              <button onClick={() => setSelectedBill(null)} className="text-gray-400 hover:text-gray-600">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-4 mb-6 pb-6 border-b">
              <div>
                <p className="text-sm text-gray-600">Patient Name</p>
                <p className="font-semibold text-lg text-gray-900">{selectedBill.patientName}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Bill Date</p>
                <p className="font-semibold text-gray-900">{new Date(selectedBill.billDate).toLocaleDateString()}</p>
              </div>
              <div>
                <p className="text-sm text-gray-600">Status</p>
                <div className="flex items-center gap-2 mt-1">
                  {getStatusIcon(selectedBill.status)}
                  <span
                    className={`font-semibold ${selectedBill.status === "PAID" ? "text-green-600" : "text-orange-600"}`}
                  >
                    {selectedBill.status}
                  </span>
                </div>
              </div>
            </div>

            {/* Amount Summary */}
            <div className="bg-gradient-to-br from-blue-50 to-blue-100 p-4 rounded-lg mb-6 border border-blue-200">
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-gray-700">Total Amount</span>
                  <span className="font-bold text-lg text-blue-600">₹{selectedBill.totalAmount?.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-green-700">
                  <span>Paid Amount</span>
                  <span className="font-semibold">₹{selectedBill.paidAmount?.toFixed(2)}</span>
                </div>
                <div className="border-t border-blue-200 pt-2 flex justify-between">
                  <span className="font-semibold text-gray-900">Balance</span>
                  <span
                    className={`font-bold text-lg ${selectedBill.balanceAmount > 0 ? "text-red-600" : "text-green-600"}`}
                  >
                    ₹{selectedBill.balanceAmount?.toFixed(2)}
                  </span>
                </div>
              </div>
            </div>

            {/* Bill Items */}
            {selectedBill.billItems && selectedBill.billItems.length > 0 && (
              <div className="mb-6 pb-6 border-b">
                <p className="font-semibold text-gray-900 mb-3">Bill Items</p>
                <div className="space-y-2 max-h-40 overflow-y-auto">
                  {selectedBill.billItems.map((item: any) => (
                    <div key={item.itemId} className="flex justify-between text-sm bg-gray-50 p-2 rounded">
                      <span className="text-gray-700">{item.description}</span>
                      <span className="font-semibold text-gray-900">₹{item.totalPrice?.toFixed(2)}</span>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Actions */}
            <div className="space-y-3">
              <div className="flex gap-2">
                <Button
                  onClick={() => handleEditBill(selectedBill)}
                  className="flex-1 bg-blue-600 hover:bg-blue-700 text-white flex items-center justify-center gap-2"
                >
                  <Edit2 className="w-4 h-4" />
                  Edit Bill
                </Button>
                <Button
                  onClick={() => handleDeleteBill(selectedBill.billId)}
                  className="flex-1 bg-red-600 hover:bg-red-700 text-white flex items-center justify-center gap-2"
                >
                  <Trash2 className="w-4 h-4" />
                  Delete
                </Button>
              </div>

              {selectedBill.balanceAmount > 0 && (
                <div>
                  <label className="block text-sm font-semibold text-gray-900 mb-2">Payment Amount</label>
                  <div className="flex gap-2">
                    <input
                      type="number"
                      value={paymentAmount}
                      onChange={(e) => setPaymentAmount(e.target.value)}
                      className="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder={`Max: ${selectedBill.balanceAmount?.toFixed(2)}`}
                      max={selectedBill.balanceAmount}
                      min="0"
                      step="0.01"
                    />
                  </div>
                  <Button
                    onClick={handleProcessPayment}
                    className="w-full mt-2 bg-green-600 hover:bg-green-700 text-white"
                  >
                    Process Payment
                  </Button>
                </div>
              )}
            </div>
          </Card>
        ) : (
          <Card className="p-6 bg-white flex items-center justify-center min-h-96">
            <div className="text-center">
              <AlertCircle className="w-12 h-12 text-gray-400 mx-auto mb-4" />
              <p className="text-gray-600">Select a bill to view details</p>
            </div>
          </Card>
        )}
      </div>

      {/* Bill Modal */}
      {showModal && (
        <BillModal
          bill={editingBill}
          onClose={() => {
            setShowModal(false)
            setEditingBill(null)
          }}
          onSave={() => {
            setShowModal(false)
            setEditingBill(null)
            fetchBills()
            setSuccess("Bill saved successfully")
            setTimeout(() => setSuccess(""), 3000)
          }}
        />
      )}
    </div>
  )
}
