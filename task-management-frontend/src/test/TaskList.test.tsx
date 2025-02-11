import { render, screen, fireEvent, waitFor } from "@testing-library/react"
import "@testing-library/jest-dom"
import { AuthProvider } from "../components/AuthContext.tsx"
import TaskList from "../components/TaskList.tsx"
import axios from "axios"

jest.mock('axios', () => ({
    __esModule: true,
    default: {
        get: jest.fn(() => Promise.resolve({ data: {} })),
    },
}));
const mockedAxios = axios as jest.Mocked<typeof axios>

const renderTaskList = () => {
    render(
        <AuthProvider>
            <TaskList />
        </AuthProvider>,
    )
}

describe("TaskList Component", () => {
    beforeEach(() => {
        mockedAxios.get.mockResolvedValue({
            data: [
                { id: 1, title: "Task 1", description: "Description 1", status: "TODO", deadline: "2023-06-30" },
                { id: 2, title: "Task 2", description: "Description 2", status: "IN_PROGRESS", deadline: "2023-07-15" },
            ],
        })
    })

    test("renders task list", async () => {
        renderTaskList()

        await waitFor(() => {
            expect(screen.getByText("Task 1")).toBeInTheDocument()
            expect(screen.getByText("Task 2")).toBeInTheDocument()
        })
    })

    test("filters tasks by status", async () => {
        renderTaskList()

        await waitFor(() => {
            expect(screen.getByText("Task 1")).toBeInTheDocument()
            expect(screen.getByText("Task 2")).toBeInTheDocument()
        })

        fireEvent.change(screen.getByRole("combobox"), { target: { value: "TODO" } })

        await waitFor(() => {
            expect(mockedAxios.get).toHaveBeenCalledWith("http://localhost:8080/tasks?status=TODO", expect.any(Object))
        })
    })

    test("searches tasks", async () => {
        renderTaskList()

        await waitFor(() => {
            expect(screen.getByText("Task 1")).toBeInTheDocument()
            expect(screen.getByText("Task 2")).toBeInTheDocument()
        })

        fireEvent.change(screen.getByPlaceholderText("Search tasks..."), { target: { value: "Task 1" } })

        expect(screen.getByText("Task 1")).toBeInTheDocument()
        expect(screen.queryByText("Task 2")).not.toBeInTheDocument()
    })
})

